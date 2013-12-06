package ordappengine;

import java.util.ArrayList;

public interface StorageManager {
	boolean isAvailable(String emailAddress);

	boolean insertPoster(Submission submission);

	BackendSession authenticateUser(String emailAddress, String password);

	boolean createUser(String emailAddress, String password, boolean isAdmin);

	boolean logout(String sessionID);

	boolean isInSession(String sessionID);

	BackendSession getSessionFromCache(String sessionID);
	
	ArrayList<Submission> getBlobServe(String emailAddress);
	
	ArrayList<Submission> getBlobServe();
	
	boolean updateStatus(String blobKey, String status);
	
	boolean deleteSubmission(String blobKey);
	
	boolean editUser(String emailAddress, String password);

	Submission getSubmission(String blobKey);
	
}