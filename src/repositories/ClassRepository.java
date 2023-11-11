package repositories;

import dto.ClassInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassRepository {
	private final Connection conn;
	private final Statement stmt;

	public ClassRepository(Connection conn, Statement stmt) {
		this.conn = conn;
		this.stmt = stmt;
	}

	public List<ClassInfo> getTakingClass(String studentId) {
		List<ClassInfo> result = new ArrayList<>();
		try {
			String sql = "\n" +
					"SELECT C.LECTURE_CODE, C.SECTION_CODE, L.NAME " +
					"FROM STUDENT S, TAKE_CLASS TC, CLASS C, LECTURE L " +
					"WHERE S.STUDENT_ID = TC.STUDENT_ID AND C.LECTURE_CODE = TC.LECTURE_CODE " +
					"   AND C.SECTION_CODE = TC.SECTION_CODE AND L.LECTURE_CODE = C.LECTURE_CODE " +
					"   AND S.STUDENT_ID = ?";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, studentId);

			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				ClassInfo classInfo = new ClassInfo(
						rs.getString(1),
						rs.getString(2),
						rs.getString(3)
				);

				result.add(classInfo);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

}
