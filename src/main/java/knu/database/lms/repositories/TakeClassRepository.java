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
			"SELECT C.LECTURE_CODE, SECTION_CODE, C.PROFESSOR_ID, P.NAME, C.DEPARTMENT_CODE, BUILDING_NUMBER, ROOM_CODE, L.NAME, CUR_STUDENT_NUMBER, MAX_STUDENT_NUMBER " +
				"FROM (CLASS C JOIN PROFESSOR P ON C.PROFESSOR_ID = P.PROFESSOR_ID) JOIN LECTURE L ON C.LECTURE_CODE = L.LECTURE_CODE " +
				"WHERE YEAR = ? AND SEMESTER = ?";
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
					rs.getString(8),
					rs.getInt(9),
					rs.getInt(10)
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
				"SELECT C.LECTURE_CODE, SECTION_CODE, C.PROFESSOR_ID, P.NAME, C.DEPARTMENT_CODE, BUILDING_NUMBER, ROOM_CODE, L.NAME, CUR_STUDENT_NUMBER, MAX_STUDENT_NUMBER " +
						"FROM (CLASS C JOIN PROFESSOR P ON C.PROFESSOR_ID = P.PROFESSOR_ID) JOIN LECTURE L ON C.LECTURE_CODE = L.LECTURE_CODE " +
						"WHERE YEAR = ? AND SEMESTER = ? AND C.LECTURE_CODE LIKE ? ";
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
					rs.getString(8),
					rs.getInt(9),
					rs.getInt(10)
			));
		}

		rs.close();
		ps.close();
		conn.close();

		return result;
	}

	public TakeClassResult takeClass(String studentId, String lectureCode, String sectionCode) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			if (takingClass(conn, studentId, lectureCode, sectionCode)) {
				throw new Exception("이미 수강신청한 수업입니다.");
			}
			if (alreadyTakedLecture(conn, studentId, lectureCode)) {
				throw new Exception("이미 수강한 과목입니다.");
			}

			String sql = "SELECT CUR_STUDENT_NUMBER, MAX_STUDENT_NUMBER " +
					"FROM CLASS WHERE LECTURE_CODE = ? AND SECTION_CODE = ?";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, lectureCode);
			ps.setString(2, sectionCode);

			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new Exception("수업이 없습니다.");
			}

			int curStudentCount = rs.getInt(1);
			int maxStudentCount = rs.getInt(2);

			if (curStudentCount >= maxStudentCount) {
				throw new Exception("수강 정원이 모두 찼습니다.");
			}

			sql = "INSERT INTO TAKE_CLASS VALUES (?, ?, ?)";
			ps = conn.prepareStatement(sql);

			ps.setString(1, studentId);
			ps.setString(2, lectureCode);
			ps.setString(3, sectionCode);

			int result = ps.executeUpdate();

			if (result == 0) {
				throw new SQLException("알 수 없음.");
			}

			conn.commit();

			ps.close();
			rs.close();
			conn.close();
		}
		catch (SQLException e) {
			assert conn != null;
			conn.rollback();
			conn.close();

			// 트리거 'LMS.TRI_TAKE_CLASS_INSERT'의 수행시 오류 == CUR_NUM <= MAX_NUM을 어김
			return TakeClassResult.failResult("수강 정원이 모두 찼습니다.");
		}
		catch (Exception e) {
			assert conn != null;
			conn.rollback();
			conn.close();

			return TakeClassResult.failResult(e.getMessage());
		}

		return TakeClassResult.successResult();
	}

	public boolean untakeClass(String studentId, String lectureCode, String sectionCode) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

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
		catch (Exception e) {
			assert conn != null;
			conn.rollback();
			conn.close();

			return false;
		}
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
