package ordappengine;

import java.util.ArrayList;

import javax.inject.Named;

import org.apache.commons.validator.routines.EmailValidator;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.blobstore.BlobKey;

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

	@ApiMethod(name = "logout")
	public void logout() {
		if (session.token != null) {
			storageManager.logout(session.token);
		}
	}
	@ApiMethod(name = "getSubmissions")
	public ArrayList<Submission> getSubmissions(@Named("emailAddress") String emailAddress){
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
	public void updateStatus(BlobKey blobkey, @Named("status") String status){
        storageManager.updateStatus(blobkey, status);
	}
	
	@ApiMethod(name = "insertPoster")
	public void insertPoster(Submission sub){
        storageManager.insertPoster(sub);
	}
}