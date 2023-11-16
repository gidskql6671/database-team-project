package knu.database.lms.dto;

public class Comment {
    public int id;
    public String content;
    public String publisherName;

    public Comment(int id, String content, String publisherId) {
        this.id = id;
        this.content = content;
        this.publisherName = publisherId;
    }
}
