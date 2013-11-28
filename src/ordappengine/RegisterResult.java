package ordappengine;

public class RegisterResult {

	public static final int REGISTER_SUCCESS = 0;
	public static final int REGISTER_ERROR_EMAIL_NOT_VALID = 1;
	public static final int REGISTER_ERROR_EMAIL_NOT_AVAILABLE = 2;
	public static final int REGISTER_ERROR_PASSWORD_NOT_VALID = 3;
	public static final int REGISTER_ERROR_PASSWORD_NOT_CONFIRMED = 4;
	public static final int REGISTER_ERROR_PASSWORD_NOT_MATCH = 5;
	public static final int REGISTER_ERROR_OTHER = 6;

	public int result;

	public RegisterResult(int result) {
		this.result = result;
	}

}
