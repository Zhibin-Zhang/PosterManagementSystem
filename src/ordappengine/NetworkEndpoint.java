package ordappengine;

import javax.inject.Named;

import org.apache.commons.validator.routines.EmailValidator;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

@Api(name = "networkendpoint", namespace = @ApiNamespace(ownerDomain = "ordappengine", ownerName = "ordappengine", packagePath = "ordappengine"))
public class NetworkEndpoint {
	private StorageManager storageManager;
	private BackendSession session;
	
	public static final int REGISTER_SUCCESS = 0;
	public static final int REGISTER_ERROR_EMAIL_NOT_VALID = 1;
	public static final int REGISTER_ERROR_EMAIL_NOT_AVAILABLE = 2;
	public static final int REGISTER_ERROR_PASSWORD_NOT_VALID = 3;
	public static final int REGISTER_ERROR_OTHER = 4;

	public NetworkEndpoint() {
		storageManager = new DatastoreControl();
		session = new BackendSession();
	}

	public NetworkEndpoint(String token) {
		storageManager = new DatastoreControl();
		session = new BackendSession(token);
	}

	@ApiMethod(name = "signIn")
	public BackendSession signIn(@Named("emailAddress")String emailAddress, @Named("password")String password) {
		if (emailAddress == null || password == null || emailAddress.isEmpty()
				|| password.isEmpty()) {
			return null;
		}

		return storageManager.authenticateUser(emailAddress, password);
	}

	@ApiMethod(name = "registerUser")
	public int registerUser(@Named("emailAddress")String emailAddress, @Named("password")String password) {
		// Check email
		if (emailAddress == null || !EmailValidator.getInstance().isValid(emailAddress)) {
			return REGISTER_ERROR_EMAIL_NOT_VALID;
		}
		
		if (!storageManager.isAvailable(emailAddress)) {
			return REGISTER_ERROR_EMAIL_NOT_AVAILABLE;
		}
		
		// Check password
		if (password == null || password.isEmpty()) {
			return REGISTER_ERROR_PASSWORD_NOT_VALID;
		}

		// Create user
		if (storageManager.createUser(emailAddress, password, false)) {
			return REGISTER_SUCCESS;
		}
		
		return REGISTER_ERROR_OTHER;
	}
	
	@ApiMethod(name = "setBackendSessionToken")
	public void setBackendSessionToken(@Named("token")String token) {
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

	@ApiMethod(name = "setStorageManager")
	public void setStorageManager(StorageManager storageManager) {
		this.storageManager = storageManager;
	}
}