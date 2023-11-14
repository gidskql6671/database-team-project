package dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassInfo {
	public String lectureCode;
	public String sectionCode;
	public String professorId;
	public String professorName;
	public String departmentCode;
	public int buildingNumber;
	public String roomCode;

	public ClassInfo(String lectureCode, String sectionCode, String professorId, String professorName, String departmentCode, int buildingNumber, String roomCode) {
		this.lectureCode = lectureCode;
		this.sectionCode = sectionCode;
		this.professorId = professorId;
		this.professorName = professorName;
		this.departmentCode = departmentCode;
		this.buildingNumber = buildingNumber;
		this.roomCode = roomCode;
	}

	public static ClassInfo fromResultSet(ResultSet rs) throws SQLException {
		return new ClassInfo(
				rs.getString(1),
				rs.getString(2),
				rs.getString(3),
				rs.getString(4),
				rs.getString(5),
				rs.getInt(6),
				rs.getString(7)
		);
	}
}
