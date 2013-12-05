package ordappengine;

public class UploadResult {

	public static final int UPLOAD_SUCCESS = 0;
	public static final int UPLOAD_MAX_SIZE_EXCEEDED = 1;
	public static final int UPLOAD_INVALID_FILE_TYPE = 2;
	public static final int UPLOAD_BACKEND_ERROR = 3;
	public static final int UPLOAD_UNAUTHENTICATED = 4;
	public static final int UPLOAD_NO_FILE = 5;
	
	public int result;

	public UploadResult(int result) {
		this.result = result;
	}
}
