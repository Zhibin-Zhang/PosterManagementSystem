package ordappengine;

import com.google.appengine.api.blobstore.BlobKey;

public class Submission {
	public static final String SUBMITTED = "submitted";
	public static final String PROCESSING = "processing";
	public static final String PRINTED = "printed";
	public static final String FINISHED = "finished";
	public static final String WRONG_FORMAT_SIZE = "wrong format/size";
	public static final String OTHER_ERRORS = "other errors";
	
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
