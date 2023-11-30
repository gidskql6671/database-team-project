package knu.database.lms.repositories;

import knu.database.lms.dto.ClassInfo;
import knu.database.lms.dto.Comment;
import knu.database.lms.dto.Post;
import knu.database.lms.dto.WriteComment;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClassRepository {
	private final DataSource dataSource;

	public ClassRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<ClassInfo> getTakingClass(String studentId) throws SQLException {
		Connection conn = dataSource.getConnection();

		List<ClassInfo> result = new ArrayList<>();
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
		conn.close();

		return result;
	}

	public boolean isTakingClass(String studentId, String lectureCode, String sectionCode) throws SQLException {
		Connection conn = dataSource.getConnection();

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
		conn.close();

		return true;
	}

	public List<Post> getPosts(String lectureCode, String sectionCode) throws SQLException {
		Connection conn = dataSource.getConnection();

		List<Post> result = new ArrayList<>();
		String sql = "SELECT POST_ID, TYPE, TITLE, NAME, CREATED_AT " +
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
					rs.getString(4),
					rs.getTimestamp(5)
			);

			result.add(post);
		}

		rs.close();
		ps.close();
		conn.close();

		return result;
	}

	public Post getPost(String lectureCode, String sectionCode, int postId) throws SQLException {
		Connection conn = dataSource.getConnection();

		Post result = null;
		String sql = "SELECT POST_ID, TYPE, TITLE, CONTENT, NAME, CREATED_AT " +
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
					rs.getString(5),
					rs.getTimestamp(6)
			);
		}

		rs.close();
		ps.close();
		conn.close();

		return result;
	}

	public List<Comment> getComments(String lectureCode, String sectionCode, int postId) throws SQLException {
		Connection conn = dataSource.getConnection();

		List<Comment> result = new ArrayList<>();
		String sql = "(SELECT C.COMMENT_ID, C.CONTENT, S.NAME, C.CREATED_AT " +
				"FROM POST P JOIN POST_COMMENT C ON P.POST_ID = C.POST_ID  " +
				"JOIN STUDENT S ON S.STUDENT_ID = C.PUBLISHER_STUDENT_ID " +
				"WHERE P.LECTURE_CODE = ? AND P.SECTION_CODE = ? AND C.POST_ID = ?) " +
				"UNION " +
				"(SELECT C.COMMENT_ID, C.CONTENT, PR.NAME, C.CREATED_AT " +
				"FROM POST P JOIN POST_COMMENT C ON P.POST_ID = C.POST_ID  " +
				"JOIN PROFESSOR PR ON PR.PROFESSOR_ID = C.PUBLISHER_PROFESSOR_ID " +
				"WHERE P.LECTURE_CODE = ? AND P.SECTION_CODE = ? AND C.POST_ID = ?) " +
				"ORDER BY CREATED_AT";
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
					rs.getString(3),
					rs.getTimestamp(4)
			);
			result.add(comment);
		}

		rs.close();
		ps.close();
		conn.close();

		return result;
	}

	public List<String> getStudents(String lectureCode, String sectionCode) throws SQLException {
		Connection conn = dataSource.getConnection();

		List<String> result = new ArrayList<>();
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
		conn.close();

		return result;
	}

	public int getStudentsCount(String lectureCode, String sectionCode) throws SQLException {
		Connection conn = dataSource.getConnection();

		int result = 0;
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
		conn.close();

		return result;
	}

	public List<ClassInfo> getClassByDayOfWeek(int dayOfWeek) throws SQLException {
		Connection conn = dataSource.getConnection();

		List<ClassInfo> result = new ArrayList<>();
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
		conn.close();

		return result;
	}

	public WriteComment writeComment(int postId, String content, String publisherStudentId) throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();

		String sql = "SELECT COMMENT_ID " +
				"FROM POST_COMMENT " +
				"WHERE ROWNUM = 1 " +
				"ORDER BY COMMENT_ID DESC";

		ResultSet rs = stmt.executeQuery(sql);
		int newCommentId;
		if (!rs.next()) {
			newCommentId = 1;
		}
		else {
			newCommentId = rs.getInt(1) + 1;
		}
		LocalDateTime createdAt = LocalDateTime.now();

		sql = "INSERT INTO POST_COMMENT VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setInt(1, newCommentId);
		ps.setDate(2, Date.valueOf(createdAt.toLocalDate()));
		ps.setString(3, content);
		ps.setInt(4, postId);
		ps.setString(5, publisherStudentId);
		ps.setNull(6, Types.VARCHAR);

		int insertResult = ps.executeUpdate();

		if (insertResult == 0) {
			return WriteComment.failWritePost("알 수 없음.");
		}
		conn.commit();

		WriteComment result = WriteComment.successWritePost();

		rs.close();
		ps.close();
		conn.close();

		return result;
	}
}
