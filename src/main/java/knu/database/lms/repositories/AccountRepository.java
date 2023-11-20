package knu.database.lms.repositories;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class AccountRepository {
	private final DataSource dataSource;

	public AccountRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String login(String loginId, String password) throws SQLException {
		Connection conn = dataSource.getConnection();
		String result = null;

		String sql =
			"SELECT STUDENT_ID " +
					"FROM STUDENT " +
					"WHERE LOGIN_ID = ? AND LOGIN_PASSWORD = ? ";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, loginId);
		ps.setString(2, password);

		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			result = rs.getString(1);
		}

		rs.close();
		ps.close();
		conn.close();

		return result;
	}

	public boolean changePassword(String studentId, String changedPassword) throws SQLException {
		Connection conn = dataSource.getConnection();

		String sql = "UPDATE STUDENT S SET S.LOGIN_PASSWORD = ? WHERE S.STUDENT_ID = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, changedPassword);
		ps.setString(2, studentId);

		int rs = ps.executeUpdate();

		conn.commit();

		ps.close();
		conn.close();

		return rs == 1;
	}

	public double getGPA(String studentId) throws SQLException {
		Connection conn = dataSource.getConnection();

		double result = 0;

		String sql = "SELECT AVG(GP.GRADE_POINT) AS GPA " +
				"FROM STUDENT S, GRADE_POINT GP " +
				"WHERE S.STUDENT_ID = GP.STUDENT_ID AND S.STUDENT_ID = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, studentId);

		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			result = rs.getDouble(1);
		}

		rs.close();
		ps.close();
		conn.close();

		return result;
	}
}
