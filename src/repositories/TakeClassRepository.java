package repositories;

import dto.ClassInfo;
import dto.TakeClassResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TakeClassRepository {
	private final Connection conn;
	private final Statement stmt;

	public TakeClassRepository(Connection conn, Statement stmt) {
		this.conn = conn;
		this.stmt = stmt;
	}

	public List<ClassInfo> getClasses(int year, String semester) {
		List<ClassInfo> result = new ArrayList<>();
		try {
			String sql =
				"SELECT LECTURE_CODE, SECTION_CODE, C.PROFESSOR_ID, NAME, C.DEPARTMENT_CODE, BUILDING_NUMBER, ROOM_CODE " +
					"FROM CLASS C JOIN PROFESSOR P ON C.PROFESSOR_ID = P.PROFESSOR_ID " +
					"WHERE YEAR = ? AND SEMESTER = ? ";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setInt(1, year);
			ps.setString(2, semester);

			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				result.add(ClassInfo.fromResultSet(rs));
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public TakeClassResult takeClass(String studentId, String lectureCode, String sectionCode) {
		try {
			if (alreadyTakingClass(studentId, lectureCode, sectionCode)) {
				return TakeClassResult.failResult("이미 수강신청한 수업입니다.");
			}
			if (alreadyTakedLecture(studentId, lectureCode)) {
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
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return TakeClassResult.successResult();
	}

	private boolean alreadyTakingClass(String studentId, String lectureCode, String sectionCode) throws SQLException {
		String sql = "SELECT * " +
				"FROM TAKE_CLASS " +
				"WHERE STUDENT_ID = ? AND LECTURE_CODE = ? AND SECTION_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, studentId);
		ps.setString(2, lectureCode);
		ps.setString(3, sectionCode);
		
		return ps.executeQuery().next();
	}

	private boolean alreadyTakedLecture(String studentId, String lectureCode) throws SQLException {
		String sql = "SELECT * FROM GRADE_POINT WHERE STUDENT_ID = ? AND LECTURE_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, studentId);
		ps.setString(2, lectureCode);

		return ps.executeQuery().next();
	}
}
