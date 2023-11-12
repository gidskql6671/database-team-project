package repositories;

import dto.ClassInfo;
import dto.Comment;
import dto.Post;

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
			String sql = "SELECT C.LECTURE_CODE, C.SECTION_CODE, L.NAME " +
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

	public boolean isTakingClass(String studentId, String lectureCode, String sectionCode) {
		try {
			String sql = "SELECT * FROM TAKE_CLASS " +
					"WHERE STUDENT_ID = ? AND LECTURE_CODE = ? AND SECTION_CODE = ?";

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, studentId);
			ps.setString(2, lectureCode);
			ps.setString(3, sectionCode);

			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				return false;
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	public List<Post> getPosts(String lectureCode, String sectionCode) {
		List<Post> result = new ArrayList<>();
		try {
			String sql = "SELECT POST_ID, TYPE, TITLE, NAME " +
					"FROM POST JOIN PROFESSOR ON PROFESSOR_ID = PUBLISHER_ID " +
					"WHERE LECTURE_CODE = ? AND SECTION_CODE = ?";

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, lectureCode);
			ps.setString(2, sectionCode);

			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				Post post = new Post(
						rs.getInt(1),
						rs.getString(2),
						rs.getString(3),
						"",
						rs.getString(4)
				);

				result.add(post);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public Post getPost(String lectureCode, String sectionCode, int postId) {
		Post result = null;
		try {
			String sql = "SELECT POST_ID, TYPE, TITLE, CONTENT, NAME " +
					"FROM POST JOIN PROFESSOR ON PROFESSOR_ID = PUBLISHER_ID " +
					"WHERE LECTURE_CODE = ? AND SECTION_CODE = ? AND POST_ID = ?";

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, lectureCode);
			ps.setString(2, sectionCode);
			ps.setInt(3, postId);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = new Post(
						rs.getInt(1),
						rs.getString(2),
						rs.getString(3),
						rs.getString(4),
						rs.getString(5)
				);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public List<Comment> getComments(String lectureCode, String sectionCode, int postId) {
		List<Comment> result = new ArrayList<>();
		try {
			String sql = "(SELECT C.COMMENT_ID, C.CONTENT, S.NAME " +
					"FROM POST P JOIN POST_COMMENT C ON P.POST_ID = C.POST_ID  " +
					"JOIN STUDENT S ON S.STUDENT_ID = C.PUBLISHER_STUDENT_ID " +
					"WHERE P.LECTURE_CODE = ? AND P.SECTION_CODE = ? AND C.POST_ID = ?) " +
					"UNION " +
					"(SELECT C.COMMENT_ID, C.CONTENT, PR.NAME " +
					"FROM POST P JOIN POST_COMMENT C ON P.POST_ID = C.POST_ID  " +
					"JOIN PROFESSOR PR ON PR.PROFESSOR_ID = C.PUBLISHER_PROFESSOR_ID " +
					"WHERE P.LECTURE_CODE = ? AND P.SECTION_CODE = ? AND C.POST_ID = ?) ";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, lectureCode);
			ps.setString(2, sectionCode);
			ps.setInt(3, postId);
			ps.setString(4, lectureCode);
			ps.setString(5, sectionCode);
			ps.setInt(6, postId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Comment comment = new Comment(
						rs.getInt(1),
						rs.getString(2),
						rs.getString(3)
				);
				result.add(comment);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public List<String> getStudents(String lectureCode, String sectionCode) {
		List<String> result = new ArrayList<>();
		try {
			String sql = "SELECT S.NAME " +
					"FROM STUDENT S, TAKE_CLASS TC, CLASS C " +
					"WHERE S.STUDENT_ID = TC.STUDENT_ID " +
					"AND C.LECTURE_CODE = TC.LECTURE_CODE " +
					"AND C.SECTION_CODE = TC.SECTION_CODE " +
					"AND C.LECTURE_CODE = ? AND C.SECTION_CODE = ?";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, lectureCode);
			ps.setString(2, sectionCode);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String student = rs.getString(1);
				result.add(student);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public int getStudentsCount(String lectureCode, String sectionCode) {
		int result = 0;
		try {
			String sql = "SELECT COUNT(*) " +
					"FROM STUDENT S, TAKE_CLASS TC, CLASS C " +
					"WHERE S.STUDENT_ID = TC.STUDENT_ID " +
					"AND C.LECTURE_CODE = TC.LECTURE_CODE " +
					"AND C.SECTION_CODE = TC.SECTION_CODE " +
					"AND C.LECTURE_CODE = ? AND C.SECTION_CODE = ? " +
					"GROUP BY C.LECTURE_CODE, C.SECTION_CODE";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, lectureCode);
			ps.setString(2, sectionCode);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public List<ClassInfo> getClassByDayOfWeek(int dayOfWeek) {
		List<ClassInfo> result = new ArrayList<>();
		try {
			String sql = "SELECT DISTINCT CT.LECTURE_CODE, CT.SECTION_CODE, L.NAME " +
					"FROM CLASS_TIME CT, LECTURE L " +
					"WHERE CT.LECTURE_CODE = L.LECTURE_CODE " +
					"AND CT.DAYOFWEEK = ?";

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setInt(1, dayOfWeek);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
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
