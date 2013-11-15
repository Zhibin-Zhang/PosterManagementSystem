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
}
