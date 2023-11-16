package knu.database.lms.dto;

public class Professor {
	public String professorId;
	public String name;
	public String departmentCode;

	public Professor(String professorId, String name, String departmentCode) {
		this.professorId = professorId;
		this.name = name;
		this.departmentCode = departmentCode;
	}
}
