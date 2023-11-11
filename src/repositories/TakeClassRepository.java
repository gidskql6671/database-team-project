package repositories;

import dto.ClassInfo;

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
}
