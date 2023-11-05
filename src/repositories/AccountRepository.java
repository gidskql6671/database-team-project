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
}
