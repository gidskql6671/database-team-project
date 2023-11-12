package repositories;

import dto.Department;
import dto.Lecture;
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
}
