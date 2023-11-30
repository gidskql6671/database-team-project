package knu.database.lms.dto;

public class ReserveClassroomResult {
    public boolean isSuccess;
    public String message;

    public ReserveClassroomResult(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public static ReserveClassroomResult successResult() {
        return new ReserveClassroomResult(true, "");
    }

    public static ReserveClassroomResult failResult(String message) {
        return new ReserveClassroomResult(false, message);
    }
}
