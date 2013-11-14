package ordappengine;

import java.util.ArrayList;

public class BackendSession {
	public String token;
	public String userName;
	public boolean isAdmin;
	public ArrayList<Submission> submissions;

	public BackendSession() {
		this.token = null;
	}

	public BackendSession(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
}
