package ordappengine;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.inject.Named;

import org.apache.commons.validator.routines.EmailValidator;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.blobstore.BlobKey;
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

	@SuppressWarnings("deprecation")
	@ApiMethod(name = "uploadPoster")
	public UploadResult uploadPoster(PosterFile file) {
		// Check if there is a file to upload
		if (file == null || file.fileAsBytes.length == 0) {
			return new UploadResult(UploadResult.UPLOAD_NO_FILE);
		}

		// Validate file type
		if (!file.mimeType.equals("image/jpeg")
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
}