package ordappengine;

public class DeleteResult {
	public static final int DELETE_SUCCESS = 0;
	public static final int DELETE_BACKEND_ERROR = 1;
	public static final int DELETE_UNPRIVILEGED = 2;
	public static final int DELETE_INVALID_KEY = 3;
	public static final int DELETE_SUBMISSION_NOT_EXISTS = 4;
	public static final int DELETE_STATUS_CONFLICT = 5;
	
	public int result;

	public DeleteResult(int result) {
		this.result = result;
	}
}
