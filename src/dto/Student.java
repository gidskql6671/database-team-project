package dto;

public class Student {
	public String studentId;
	public String name;
	public int grade;
	public String departmentCode;

	public Student(String studentId, String name, int grade, String departmentCode) {
		this.studentId = studentId;
		this.name = name;
		this.grade = grade;
		this.departmentCode = departmentCode;
	}
}
