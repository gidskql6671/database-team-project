package knu.database.lms.dto;

public class ClassInfo {
	public String lectureCode;
	public String sectionCode;
	public String professorId;
	public String professorName;
	public String departmentCode;
	public int buildingNumber;
	public String roomCode;
	public String lectureName;

	public ClassInfo(String lectureCode, String sectionCode, String professorId, String professorName, String departmentCode, int buildingNumber, String roomCode, String lectureName) {
		this.lectureCode = lectureCode;
		this.sectionCode = sectionCode;
		this.professorId = professorId;
		this.professorName = professorName;
		this.departmentCode = departmentCode;
		this.buildingNumber = buildingNumber;
		this.roomCode = roomCode;
		this.lectureName = lectureName;
	}

	public ClassInfo(String lectureCode, String sectionCode, String professorId, String professorName, String departmentCode, int buildingNumber, String roomCode) {
		this(
				lectureCode,
				sectionCode,
				professorId,
				professorName,
				departmentCode,
				buildingNumber,
				roomCode,
				""
		);
	}
	public ClassInfo(String lectureCode, String sectionCode, String lectureName) {
		this(
				lectureCode,
				sectionCode,
				"",
				"",
				"",
				0,
				"",
				lectureName
		);
	}
}
