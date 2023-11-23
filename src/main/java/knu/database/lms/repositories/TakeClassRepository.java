package knu.database.lms.repositories;

import knu.database.lms.dto.ClassInfo;
import knu.database.lms.dto.TakeClassResult;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TakeClassRepository {
	private final DataSource dataSource;

	public TakeClassRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<ClassInfo> getClasses(int year, String semester) throws SQLException {
		Connection conn = dataSource.getConnection();

		String sql =
			"SELECT LECTURE_CODE, SECTION_CODE, C.PROFESSOR_ID, NAME, C.DEPARTMENT_CODE, BUILDING_NUMBER, ROOM_CODE " +
				"FROM CLASS C JOIN PROFESSOR P ON C.PROFESSOR_ID = P.PROFESSOR_ID " +
				"WHERE YEAR = ? AND SEMESTER = ? ";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setInt(1, year);
		ps.setString(2, semester);

		ResultSet rs = ps.executeQuery();

		List<ClassInfo> result = new ArrayList<>();
		while(rs.next()) {
			result.add(new ClassInfo(
					rs.getString(1),
					rs.getString(2),
					rs.getString(3),
					rs.getString(4),
					rs.getString(5),
					rs.getInt(6),
					rs.getString(7),
					""
			));
		}

		rs.close();
		ps.close();
		conn.close();

		return result;
	}

	public List<ClassInfo> getClassesOf(String departmentCode, int year, String semester) throws SQLException {
		Connection conn = dataSource.getConnection();

		String sql =
				"SELECT LECTURE_CODE, SECTION_CODE, C.PROFESSOR_ID, NAME, C.DEPARTMENT_CODE, BUILDING_NUMBER, ROOM_CODE " +
						"FROM CLASS C JOIN PROFESSOR P ON C.PROFESSOR_ID = P.PROFESSOR_ID " +
						"WHERE YEAR = ? AND SEMESTER = ? AND LECTURE_CODE LIKE ? ";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setInt(1, year);
		ps.setString(2, semester);
		ps.setString(3, departmentCode + "%");

		ResultSet rs = ps.executeQuery();

		List<ClassInfo> result = new ArrayList<>();
		while(rs.next()) {
			result.add(new ClassInfo(
					rs.getString(1),
					rs.getString(2),
					rs.getString(3),
					rs.getString(4),
					rs.getString(5),
					rs.getInt(6),
					rs.getString(7),
					""
			));
		}

		rs.close();
		ps.close();
		conn.close();

		return result;
	}

	public TakeClassResult takeClass(String studentId, String lectureCode, String sectionCode) throws SQLException {
		Connection conn = dataSource.getConnection();

		if (takingClass(conn, studentId, lectureCode, sectionCode)) {
			return TakeClassResult.failResult("이미 수강신청한 수업입니다.");
		}
		if (alreadyTakedLecture(conn, studentId, lectureCode)) {
			return TakeClassResult.failResult("이미 수강한 과목입니다.");
		}

		String sql = "SELECT COUNT(*) " +
				"FROM TAKE_CLASS " +
				"WHERE LECTURE_CODE = ? AND SECTION_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, lectureCode);
		ps.setString(2, sectionCode);

		ResultSet rs = ps.executeQuery();
		if (!rs.next()) {
			return TakeClassResult.failResult("수업이 없습니다.");
		}

		int studentCount = rs.getInt(1);

		sql = "SELECT MAX_STUDENT_NUMBER FROM CLASS WHERE LECTURE_CODE = ? AND SECTION_CODE = ?";
		ps = conn.prepareStatement(sql);

		ps.setString(1, lectureCode);
		ps.setString(2, sectionCode);

		rs = ps.executeQuery();
		if (!rs.next()) {
			return TakeClassResult.failResult("수업이 없습니다.");
		}

		int maxStudentCount = rs.getInt(1);

		if (studentCount == maxStudentCount) {
			return TakeClassResult.failResult("수강 정원이 모두 찼습니다.");
		}

		sql = "INSERT INTO TAKE_CLASS VALUES (?, ?, ?)";
		ps = conn.prepareStatement(sql);

		ps.setString(1, studentId);
		ps.setString(2, lectureCode);
		ps.setString(3, sectionCode);

		int result = ps.executeUpdate();

		if (result == 0) {
			return TakeClassResult.failResult("알 수 없음.");
		}

		conn.commit();

		rs.close();
		ps.close();
		conn.close();

		return TakeClassResult.successResult();
	}

	public boolean untakeClass(String studentId, String lectureCode, String sectionCode) throws SQLException {
		Connection conn = dataSource.getConnection();

		String sql = "DELETE FROM TAKE_CLASS " +
				"WHERE STUDENT_ID = ? AND LECTURE_CODE = ? AND SECTION_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, studentId);
		ps.setString(2, lectureCode);
		ps.setString(3, sectionCode);

		int rs = ps.executeUpdate();

		conn.commit();

		ps.close();
		conn.close();

		return rs == 1;
	}

	public boolean takingClass(String studentId, String lectureCode, String sectionCode) throws SQLException {
		Connection conn = dataSource.getConnection();

		String sql = "SELECT * " +
				"FROM TAKE_CLASS " +
				"WHERE STUDENT_ID = ? AND LECTURE_CODE = ? AND SECTION_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, studentId);
		ps.setString(2, lectureCode);
		ps.setString(3, sectionCode);

		ResultSet rs = ps.executeQuery();
		boolean result = rs.next();

		rs.close();
		ps.close();
		conn.close();

		return result;
	}

	private boolean takingClass(Connection conn, String studentId, String lectureCode, String sectionCode) throws SQLException {
		String sql = "SELECT * " +
				"FROM TAKE_CLASS " +
				"WHERE STUDENT_ID = ? AND LECTURE_CODE = ? AND SECTION_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, studentId);
		ps.setString(2, lectureCode);
		ps.setString(3, sectionCode);

		ResultSet rs = ps.executeQuery();
		boolean result = rs.next();

		rs.close();
		ps.close();

		return result;
	}

	private boolean alreadyTakedLecture(Connection conn, String studentId, String lectureCode) throws SQLException {
		String sql = "SELECT * FROM GRADE_POINT WHERE STUDENT_ID = ? AND LECTURE_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, studentId);
		ps.setString(2, lectureCode);


		ResultSet rs = ps.executeQuery();
		boolean result = rs.next();

		rs.close();
		ps.close();

		return result;
	}
}
