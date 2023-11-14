package dto;

public class WriteComment {
    public boolean isSuccess;
    public String message;

    public WriteComment(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public static WriteComment successWritePost() {
        return new WriteComment(true, "");
    }

    public static WriteComment failWritePost(String message) {
        return new WriteComment(false, message);
    }
}
