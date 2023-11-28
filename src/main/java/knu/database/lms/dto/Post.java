package knu.database.lms.dto;

import java.sql.Timestamp;

public class Post {
	public int id;
	public String type;
	public String title;
	public String content;
	public String publisherName;
	public Timestamp createdAt;

	public Post(int id, String type, String title, String content, String publisherName, Timestamp createdAt) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.content = content;
		this.publisherName = publisherName;
		this.createdAt = createdAt;
	}
}
