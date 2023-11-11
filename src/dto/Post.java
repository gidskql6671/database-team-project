package dto;

public class Post {
	public int id;
	public String type;
	public String title;
	public String content;
	public String publisherName;

	public Post(int id, String type, String title, String content, String publisherName) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.content = content;
		this.publisherName = publisherName;
	}
}
