package ordappengine;

import java.util.ArrayList;

import com.google.appengine.api.blobstore.BlobKey;

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
	
	boolean updateStatus(BlobKey blobKey, String status);
	
	boolean deleteSumission(BlobKey blobKey);
	
	boolean editUser(String emailAddress, String password);
	
}