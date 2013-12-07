package ordappengine;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.inject.Named;

import org.apache.commons.validator.routines.EmailValidator;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

@Api(name = "networkendpoint", namespace = @ApiNamespace(ownerDomain = "ordappengine", ownerName = "ordappengine", packagePath = "ordappengine"))
public class NetworkEndpoint {
	private StorageManager storageManager;
	private BackendSession session;

	public NetworkEndpoint() {
		storageManager = new DatastoreControl();
		session = new BackendSession();
	}

	public NetworkEndpoint(String token) {
		storageManager = new DatastoreControl();
		session = new BackendSession(token);
	}

	@ApiMethod(name = "signIn")
	public BackendSession signIn(@Named("emailAddress") String emailAddress,
			@Named("password") String password) {
		if (emailAddress == null || password == null || emailAddress.isEmpty()
				|| password.isEmpty()) {
			return null;
		}

		return storageManager.authenticateUser(emailAddress, password);
	}

	@ApiMethod(name = "registerUser")
	public RegisterResult registerUser(
			@Named("emailAddress") String emailAddress,
			@Named("password") String password,
			@Named("confirmPassword") String confirmPassword) {
		// Check email
		if (emailAddress == null
				|| !EmailValidator.getInstance().isValid(emailAddress)) {
			return new RegisterResult(
					RegisterResult.REGISTER_ERROR_EMAIL_NOT_VALID);
		}

		if (!storageManager.isAvailable(emailAddress)) {
			return new RegisterResult(
					RegisterResult.REGISTER_ERROR_EMAIL_NOT_AVAILABLE);
		}

		// Check password
		if (password == null || password.isEmpty() || password.length() < 6
				|| password.length() > 12) {
			return new RegisterResult(
					RegisterResult.REGISTER_ERROR_PASSWORD_NOT_VALID);
		}

		if (confirmPassword == null || confirmPassword.isEmpty()) {
			return new RegisterResult(
					RegisterResult.REGISTER_ERROR_PASSWORD_NOT_CONFIRMED);
		}

		if (!password.equals(confirmPassword)) {
			return new RegisterResult(
					RegisterResult.REGISTER_ERROR_PASSWORD_NOT_MATCH);
		}

		// Create user
		if (storageManager.createUser(emailAddress, password, false)) {
			return new RegisterResult(RegisterResult.REGISTER_SUCCESS);
		}

		return new RegisterResult(RegisterResult.REGISTER_ERROR_OTHER);
	}

	@ApiMethod(name = "setBackendSessionToken")
	public void setBackendSessionToken(@Named("token") String token) {
		session.token = token;
	}

	@ApiMethod(name = "authenticateSession")
	public BackendSession authenticateSession() {
		if (session.token != null) {
			return storageManager.getSessionFromCache(session.token);
		}

		return null;
	}
	
	@ApiMethod(name = "deletePoster")
	public synchronized DeleteResult deletePoster(@Named("blobKeyString") String blobKeyString) {
		if (blobKeyString == null) {
			return new DeleteResult(DeleteResult.DELETE_INVALID_KEY);
		}
		
		// Obtain submission information
		Submission submission = storageManager.getSubmission(blobKeyString);
		
		if (submission == null) {
			return new DeleteResult(DeleteResult.DELETE_SUBMISSION_NOT_EXISTS);
		}
		
		// Check for privileges
		BackendSession tempSession = null;
		boolean privileged = false;
		
		if (session.token != null) {
			tempSession = storageManager.getSessionFromCache(session.token);

			if (tempSession != null) {
				if (tempSession.isAdmin || tempSession.emailAddress.equals(submission.username)) {
					privileged = true;
				}
			}
		}

		if (!privileged) {
			return new DeleteResult(DeleteResult.DELETE_UNPRIVILEGED);
		}
		
		// Check that the status is submitted (restriction applicable only if user is not an admin)
		if (!tempSession.isAdmin && !submission.posterStatus.equals(Submission.SUBMITTED)) {
			return new DeleteResult(DeleteResult.DELETE_STATUS_CONFLICT);
		}
		
		// Delete submission from datastore
		if (!storageManager.deleteSubmission(blobKeyString)) {
			return new DeleteResult(DeleteResult.DELETE_BACKEND_ERROR);
		}
		
		// Delete file from blobstore
		// See: http://stackoverflow.com/questions/13208504/delete-files-from-blobstore-using-file-serving-url
		BlobstoreService blobService = BlobstoreServiceFactory.getBlobstoreService();
		blobService.delete(new BlobKey(blobKeyString));
		
		return new DeleteResult(DeleteResult.DELETE_SUCCESS);
	}

	@SuppressWarnings("deprecation")
	@ApiMethod(name = "uploadPoster")
	public UploadResult uploadPoster(PosterFile file) {
		// Check if there is a file to upload
		if (file == null || file.fileAsBytes.length == 0) {
			return new UploadResult(UploadResult.UPLOAD_NO_FILE);
		}

		// Validate file type
		if (!file.mimeType.equals("image/jpeg")
				&& !file.mimeType.equals("image/pjpeg")
				&& !file.mimeType.equals("application/vnd.ms-powerpoint")
				&& !file.mimeType
						.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")
				&& !file.mimeType.equals("application/pdf")
				&& !file.mimeType.equals("image/png")) {
			return new UploadResult(UploadResult.UPLOAD_INVALID_FILE_TYPE);
		}

		// Validate the file size
		if (file.fileAsBytes.length > 1024 * 1024 * 7) {
			return new UploadResult(UploadResult.UPLOAD_MAX_SIZE_EXCEEDED);
		}

		// Obtain the user's email address
		String emailAddress = null;

		if (session.token != null) {
			BackendSession tempSession = storageManager
					.getSessionFromCache(session.token);

			if (tempSession != null) {
				emailAddress = tempSession.emailAddress;
			}
		}

		if (emailAddress == null) {
			return new UploadResult(UploadResult.UPLOAD_UNAUTHENTICATED);
		}

		try {
			// Upload to blobstore:
			// https://developers.google.com/appengine/docs/java/blobstore/
			FileService fileService = FileServiceFactory.getFileService();
			AppEngineFile appEngineFile = fileService
					.createNewBlobFile(file.mimeType);
			FileWriteChannel writeChannel = fileService.openWriteChannel(
					appEngineFile, true);

			writeChannel.write(ByteBuffer.wrap(file.fileAsBytes));
			writeChannel.closeFinally();

			BlobKey blobKey = fileService.getBlobKey(appEngineFile);

			// Store submission
			Submission submission = new Submission();

			submission.blobKey = blobKey.getKeyString();
			submission.posterName = file.name;
			submission.username = emailAddress;

			if (storageManager.insertPoster(submission)) {
				return new UploadResult(UploadResult.UPLOAD_SUCCESS);
			} else {
				return new UploadResult(UploadResult.UPLOAD_BACKEND_ERROR);
			}
		} catch (Exception e) {
			return new UploadResult(UploadResult.UPLOAD_BACKEND_ERROR);
		}
	}

	@ApiMethod(name = "logout")
	public void logout() {
		if (session.token != null) {
			storageManager.logout(session.token);
		}
	}

	@ApiMethod(name = "getSubmissions")
	public ArrayList<Submission> getSubmissions(
			@Named("emailAddress") String emailAddress) {
		return storageManager.getBlobServe(emailAddress);
	}

	@ApiMethod(name = "getAllSubmissions")
	public ArrayList<Submission> getAllSubmissions() {
		return storageManager.getBlobServe();
	}

	@ApiMethod(name = "setStorageManager")
	public void setStorageManager(StorageManager storageManager) {
		this.storageManager = storageManager;
	}

	@ApiMethod(name = "updateStatus")
	public void updateStatus(@Named("blobKey") String blobkey, @Named("status") String status) {
		storageManager.updateStatus(blobkey, status);
	}
	

	@ApiMethod(name = "insertPoster")
	public void insertPoster(Submission sub) {
		storageManager.insertPoster(sub);
	}
	@ApiMethod(name = "editUser")
	public User editUser(@Named("emailAddress") String emailAddress, @Named("password") String password){
		return storageManager.editUser(emailAddress, password);
	}
	@ApiMethod(name = "getUser")
	public User getUser(@Named("emailAddress") String emailAddress){
		return storageManager.getUser(emailAddress);
	}
	@ApiMethod(name = "filterSubmissions")
	public ArrayList<Submission> filterSubmissions(@Named("filter") String filter){
		ArrayList<Submission> temp = this.getAllSubmissions();
		ArrayList<Submission> returningList = new ArrayList<Submission>();
		if(filter.equals(Submission.FINISHED)){
			for(int i = 0; i<temp.size();i++){
				if(temp.get(i).posterStatus.equals((Submission.FINISHED))){
					returningList.add(temp.get(i));
				}
			}			
		}else if(filter.equals(Submission.OTHER_ERRORS)){
			for(int i = 0; i<temp.size();i++){
				if(temp.get(i).posterStatus.equals((Submission.OTHER_ERRORS))){
					returningList.add(temp.get(i));
				}
			}
		}else if(filter.equals(Submission.PRINTED)){
			for(int i = 0; i<temp.size();i++){
				if(temp.get(i).posterStatus.equals((Submission.PRINTED))){
					returningList.add(temp.get(i));
				}
			}
		}else if(filter.equals(Submission.PROCESSING)){
			for(int i = 0; i<temp.size();i++){
				if(temp.get(i).posterStatus.equals((Submission.PROCESSING))){
					returningList.add(temp.get(i));
				}
			}
		}else if(filter.equals(Submission.SUBMITTED)){
			for(int i = 0; i<temp.size();i++){
				if(temp.get(i).posterStatus.equals((Submission.SUBMITTED))){
					returningList.add(temp.get(i));
				}
			}
		}else if(filter.equals(Submission.WRONG_FORMAT_SIZE)){
			for(int i = 0; i<temp.size();i++){
				if(temp.get(i).posterStatus.equals((Submission.WRONG_FORMAT_SIZE))){
					returningList.add(temp.get(i));
				}
			}
		}
		return returningList;
	}
}