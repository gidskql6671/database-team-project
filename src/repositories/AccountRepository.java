package repositories;

import java.sql.*;

public class AccountRepository {
	private final Connection conn;
	private final Statement stmt;

	public AccountRepository(Connection conn, Statement stmt) {
		this.conn = conn;
		this.stmt = stmt;
	}

	public String login(String loginId, String password) {
		String result = "";
		try {
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
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public boolean changePassword(String studentId, String changedPassword) {
		int rs = 0;
		try {
			String sql = "UPDATE STUDENT S SET S.LOGIN_PASSWORD = ? WHERE S.STUDENT_ID = ?";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, changedPassword);
			ps.setString(2, studentId);

			rs = ps.executeUpdate();

			conn.commit();

			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return rs == 1;
	}

	public double getGPA(String studentId) {
		double result = 0;
		try {
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
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}
}
