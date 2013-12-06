package ordappengine;

import java.util.ArrayList;

public class DatastoreControlStub implements StorageManager {

	@Override
	public boolean isAvailable(String emailAddress) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insertPoster(Submission submission) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BackendSession authenticateUser(String emailAddress, String password) {
		if (emailAddress.equals("admin@gmail.com") && password.equals("admin")) {
			BackendSession newSession = new BackendSession();

			newSession.emailAddress = "admin@gmail.com";
			newSession.isAdmin = true;
			newSession.submissions = null;
			newSession.token = "1234567890";

			return newSession;
		}

		return null;
	}

	@Override
	public boolean createUser(String emailAddress, String password,
			boolean isAdmin) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean logout(String sessionID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInSession(String sessionID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BackendSession getSessionFromCache(String sessionID) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean updateStatus(String blobKey, String status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteSubmission(String blobKey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<Submission> getBlobServe(String emailAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Submission> getBlobServe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean editUser(String emailAddress, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Submission getSubmission(String blobKey) {
		// TODO Auto-generated method stub
		return null;
	}

}

