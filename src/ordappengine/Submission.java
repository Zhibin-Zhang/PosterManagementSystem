package ordappengine;

import com.google.appengine.api.blobstore.BlobKey;

public class Submission {
	public String username;
	public String posterName;
	public String posterStatus;
	public BlobKey blobKey;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPosterName() {
		return posterName;
	}
	public void setPosterName(String posterName) {
		this.posterName = posterName;
	}
	public String getPosterStatus() {
		return posterStatus;
	}
	public void setPosterStatus(String posterStatus) {
		this.posterStatus = posterStatus;
	}
	public BlobKey getBlobKey() {
		return blobKey;
	}
	public void setBlobKey(BlobKey blobKey) {
		this.blobKey = blobKey;
	}
	
	
}
