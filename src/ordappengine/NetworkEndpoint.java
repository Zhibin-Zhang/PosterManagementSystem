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
		BackendSession newSession = storage.authenticateUser(user, password);
		//boolean test = storage.authenticateUser(user, password);
		// Authenticate in database. This didn't work. It's giving me errors!!!
		if (newSession != null) {
			/* I should be able to call a method like StorageManager.createSession(user)
			   It should give me back a BackendSession object with a randomly generated
			   token. This token should get stored in the database to keep track of the
			   session */
			//newSession.setToken("as84h39hb304nr55");
		}
		
		/* I am returning an object that wraps the session token.
		   In future requests, I expect to be passed this token either by way of
		   NetworkEndpoint(String token), or by setBackendSessionToken(String token).
		   This way, I can keep track of logged in users. */
		
		return newSession;
	}
	
	public void setBackendSessionToken(String token) {
		session.setToken(token);
	}
	
	public SessionType getSessionType() {
		// Here, I should be able to call a method in StorageManager to check that the
		// session is registered and return the user registered in that session.
		// The method should look like getSessionUser(BackendSession). It should return
		// null if the session does not exist. Otherwise, it should return the user
		// registered in the session.
		if (session.getToken().equals("as84h39hb304nr55")) {
			// Here, I should be able to call a method in StorageManager to check if a
			// user is an administrator. It should look like isAdmin(user).
			if (true) {
				return new SessionType("ADMIN");
			} else {
				return new SessionType("NORMAL");
			}
		}
		
		return new SessionType("NO_SESSION");
	}
	
	public void logout() {
		// Here, I should be able to call a method in StorageManager to delete the
		// session from the database if it exists. It should look like
		// destroySession(BackendSession)
	}
}
