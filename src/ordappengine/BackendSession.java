package ordappengine;

import java.util.ArrayList;
import java.io.Serializable;

public class BackendSession implements Serializable {
	private static final long serialVersionUID = 1L;
	public String token;
	public String emailAddress;
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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public ArrayList<Submission> getSubmissions() {
		return submissions;
	}

	public void setSubmissions(ArrayList<Submission> submissions) {
		this.submissions = submissions;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
