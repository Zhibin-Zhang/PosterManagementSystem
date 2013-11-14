package ordappengine;


public interface StorageManager {
	boolean isAvailable(String username);
	boolean insertPoster(String username, Submission submission);
	BackendSession authenticateUser(String username, String password);
	boolean createUser(String username, String passowrd, boolean isAdmin);
	boolean logout(String sessionID);
	boolean isInSession(String sessionID);
}
