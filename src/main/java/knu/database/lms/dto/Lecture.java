package knu.database.lms.dto;

public class Lecture {
	public String lectureCode;
	public String name;
	public int credits;

	public Lecture(String lectureCode, String name, int credits) {
		this.lectureCode = lectureCode;
		this.name = name;
		this.credits = credits;
	}
}
