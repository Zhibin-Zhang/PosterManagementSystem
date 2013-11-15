package ordappengine;

public interface StorageManager {
	boolean isAvailable(String emailAddress);

	boolean insertPoster(String emailAddress, Submission submission);

	BackendSession authenticateUser(String emailAddress, String password);

	boolean createUser(String emailAddress, String password, boolean isAdmin);

	boolean logout(String sessionID);

	boolean isInSession(String sessionID);

	BackendSession getSessionFromCache(String sessionID);
}
