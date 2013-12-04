package ordappengine;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.appengine.api.blobstore.BlobKey;

@Entity(name = "submission")
public class Submission {
	public static final String SUBMITTED = "submitted";
	public static final String PROCESSING = "processing";
	public static final String PRINTED = "printed";
	public static final String FINISHED = "finished";
	public static final String WRONG_FORMAT_SIZE = "wrong format/size";
	public static final String OTHER_ERRORS = "other errors";
	
	@Id
	public String blobKey;
	@Basic
	public String username;
	@Basic
	public String posterName;
	@Basic
	public String posterStatus = Submission.SUBMITTED;
	
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
	public String getBlobKey() {
		return blobKey;
	}
	public void setBlobKey(String blobKey) {
		this.blobKey = blobKey;
	}
	
	
}
