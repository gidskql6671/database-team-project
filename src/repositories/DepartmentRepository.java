package repositories;

import dto.Department;
import dto.Lecture;
import dto.Professor;
import dto.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentRepository {
	private final Connection conn;
	private final Statement stmt;

	public DepartmentRepository(Connection conn, Statement stmt) {
		this.conn = conn;
		this.stmt = stmt;
	}

	public Department getDepartment(String departmentCode) throws SQLException {
		String sql = "SELECT NAME, TOTAL_CREDITS_REQUIRED FROM DEPARTMENT WHERE DEPARTMENT.DEPARTMENT_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, departmentCode);

		ResultSet rs = ps.executeQuery();

		if (!rs.next()) {
			return null;
		}

		Department department = new Department(
				departmentCode,
				rs.getString(1),
				rs.getInt(2)
		);

		rs.close();
		ps.close();

		return department;
	}

	public List<Lecture> getClassLectures(String departmentCode) throws SQLException {
		List<Lecture> lectures = new ArrayList<>();

		String sql = "SELECT LECTURE_CODE, NAME, CREDITS FROM LECTURE WHERE LECTURE_CODE LIKE ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, departmentCode + "%");

		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			lectures.add(new Lecture(
					rs.getString(1),
					rs.getString(2),
					rs.getInt(3)
			));
		}

		rs.close();
		ps.close();

		return lectures;
	}

	public List<Student> getStudents(String departmentCode) throws SQLException {
		List<Student> students = new ArrayList<>();

		String sql = "SELECT S.STUDENT_ID, S.NAME, S.GRADE, D.DEPARTMENT_CODE " +
				" FROM DEPARTMENT D, STUDENT S WHERE D.DEPARTMENT_CODE = S.DEPARTMENT_CODE AND D.DEPARTMENT_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, departmentCode);

		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			students.add(new Student(
					rs.getString(1),
					rs.getString(2),
					rs.getInt(3),
					rs.getString(4)
			));
		}

		rs.close();
		ps.close();

		return students;
	}

	public List<Student> getHavingGradeStudents(String departmentCode) throws SQLException {
		List<Student> students = new ArrayList<>();

		String sql = "SELECT S.STUDENT_ID, S.NAME, S.GRADE FROM STUDENT S " +
				"WHERE S.DEPARTMENT_CODE = ? " +
				"   AND EXISTS (SELECT * FROM GRADE_POINT GP WHERE GP.STUDENT_ID = S.STUDENT_ID)";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, departmentCode);

		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			students.add(new Student(
					rs.getString(1),
					rs.getString(2),
					rs.getInt(3),
					departmentCode
			));
		}

		rs.close();
		ps.close();

		return students;
	}

	public int countProfessors(String departmentCode) throws SQLException {
		String sql = "SELECT D.NAME, COUNT(*) " +
				"FROM DEPARTMENT D, PROFESSOR P " +
				"WHERE D.DEPARTMENT_CODE = P.DEPARTMENT_CODE AND D.DEPARTMENT_CODE = ? " +
				"GROUP BY D.DEPARTMENT_CODE, D.NAME";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, departmentCode);

		ResultSet rs = ps.executeQuery();

		if (!rs.next()) {
			return 0;
		}
		int count = rs.getInt(2);

		rs.close();
		ps.close();

		return count;
	}

	public List<Professor> getNotTeachProfessors(String departmentCode, int year, String semester) throws SQLException {
		String sql = "SELECT P.PROFESSOR_ID, P.NAME " +
				"FROM PROFESSOR P " +
				"WHERE P.DEPARTMENT_CODE = ? AND " +
				"  NOT EXISTS (SELECT * FROM CLASS C WHERE C.PROFESSOR_ID = P.PROFESSOR_ID AND C.YEAR = ? AND C.SEMESTER = ?)";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, departmentCode);
		ps.setInt(2, year);
		ps.setString(3, semester);

		ResultSet rs = ps.executeQuery();

		List<Professor> professors = new ArrayList<>();
		while(rs.next()) {
			professors.add(new Professor(
					rs.getString(1),
					rs.getString(2),
					departmentCode
			));
		}

		rs.close();
		ps.close();

		return professors;
	}

	public List<Professor> getProfessors(String departmentCode) throws SQLException {
		String sql = "SELECT PROFESSOR_ID, NAME FROM PROFESSOR WHERE DEPARTMENT_CODE = ?";
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, departmentCode);

		ResultSet rs = ps.executeQuery();

		List<Professor> professors = new ArrayList<>();
		while(rs.next()) {
			professors.add(new Professor(
					rs.getString(1),
					rs.getString(2),
					departmentCode
			));
		}

		rs.close();
		ps.close();

		return professors;
	}
}
