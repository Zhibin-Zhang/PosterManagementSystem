package ordappengine;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

@Api(name = "networkendpoint", namespace = @ApiNamespace(ownerDomain = "example.com", ownerName = "example.com", packagePath = "ordappengine"))
public class NetworkEndpoint {
	private StorageManager storage;
	private BackendSession session;

	public NetworkEndpoint() {
		storage = new DatastoreControl();
		session = new BackendSession();
	}

	public NetworkEndpoint(String token) {
		storage = new DatastoreControl();
		session = new BackendSession(token);
	}

	public BackendSession signIn(String user, String password) {
		return storage.authenticateUser(user, password);
	}

	public void setBackendSessionToken(String token) {
		session.token = token;
	}

	public BackendSession authenticateSession() {
		if (session.token != null) {
			return storage.getSessionFromCache(session.token);
		}

		return null;
	}

	public void logout() {
		if (session.token != null) {
			storage.logout(session.token);
		}
	}
}