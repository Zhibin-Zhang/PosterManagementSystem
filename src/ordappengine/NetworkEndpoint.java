package ordappengine;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

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
	public BackendSession signIn(@Named("emailAddress")String emailAddress, @Named("password")String password) {
		if (emailAddress == null || password == null || emailAddress.isEmpty()
				|| password.isEmpty()) {
			return null;
		}

		return storageManager.authenticateUser(emailAddress, password);
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