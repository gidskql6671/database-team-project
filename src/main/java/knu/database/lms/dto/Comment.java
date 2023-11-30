package knu.database.lms.dto;

import java.sql.Timestamp;

public class Comment {
    public int id;
    public String content;
    public String publisherName;
    public Timestamp createdAt;

    public Comment(int id, String content, String publisherId, Timestamp createdAt) {
        this.id = id;
        this.content = content;
        this.publisherName = publisherId;
        this.createdAt = createdAt;
    }
}
