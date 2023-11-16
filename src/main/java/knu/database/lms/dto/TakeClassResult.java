package knu.database.lms.dto;

public class TakeClassResult {
	public boolean isSuccess;
	public String message;

	public TakeClassResult(boolean isSuccess, String message) {
		this.isSuccess = isSuccess;
		this.message = message;
	}

	public static TakeClassResult successResult() {
		return new TakeClassResult(true, "");
	}

	public static TakeClassResult failResult(String message) {
		return new TakeClassResult(false, message);
	}
}
