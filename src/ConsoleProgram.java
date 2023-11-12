import dto.*;
import repositories.AccountRepository;
import repositories.DepartmentRepository;
import repositories.TakeClassRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class ConsoleProgram {
	public static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
	public static final String USER_UNIVERSITY ="lms";
	public static final String USER_PASSWD ="lms";

	Scanner sc;
	private String loginedStudentId = "";
	private AccountRepository accountRepository;
	private TakeClassRepository takeClassRepository;
	private DepartmentRepository departmentRepository;


	public ConsoleProgram() {}

	public void run() {
		try {
			System.out.print("Driver Loading: ");
			// Load a JDBC driver for Oracle DBMS
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// Get a Connection object
			System.out.println("Success!");
		}catch(ClassNotFoundException e) {
			System.err.println("error = " + e.getMessage());
			System.exit(1);
		}

		Connection conn = null;
		Statement stmt = null;
		try{
			conn = DriverManager.getConnection(URL, USER_UNIVERSITY, USER_PASSWD);
			System.out.println("Oracle Connected.");
		}catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Cannot get a connection: " + ex.getLocalizedMessage());
			System.err.println("Cannot get a connection: " + ex.getMessage());
			System.exit(1);
		}

		try {
			conn.setAutoCommit(false); // auto-commit disabled
			stmt = conn.createStatement();

			sc = new Scanner(System.in);

			// 레포지토리 객체 생성
			accountRepository = new AccountRepository(conn, stmt);
			takeClassRepository = new TakeClassRepository(conn, stmt);
			departmentRepository = new DepartmentRepository(conn, stmt);

			System.out.println("\n\n통합 LMS입니다.");

			while(true) {
				System.out.println();
				System.out.println("1. 회원 기능");
				System.out.println("2. 수강신청 기능");
				System.out.println("3. 수업 기능");
				System.out.println("4. 강의실 기능");
				System.out.println("5. 학과 기능");
				System.out.println("0. 나가기");

				System.out.print("수행할 기능을 입력해주세요 : ");
				int menu = sc.nextInt();

				if (menu == 0) {
					System.out.println("시스템을 종료합니다.");

					break;
				}
				else if (menu == 1) {
					accountService();
				}
				else if (menu == 2) {
					takeClassService();

				}
				else if (menu == 3) {
					classService();

				}
				else if (menu == 4) {
					classroomService();

				}
				else if (menu == 5) {
					departmentService();
				}

				System.out.println();
			}

			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void accountService() throws SQLException {
		while (true) {
			System.out.println();
			System.out.println("1. 로그인 (아이디 : dong, 비밀번호 : dong)");
			System.out.println("2. 로그아웃");
			System.out.println("3. 비밀번호 변경");
			System.out.println("4. 평균 평점을 조회한다. (18번쿼리)");
			System.out.println("0. 뒤로 가기");
			System.out.print("[회원 기능] 수행할 기능을 입력해주세요 : ");
			int menu = sc.nextInt();
			System.out.println();

			if (menu == 0) {
				break;
			}
			else if (menu == 1) {
				if (!loginedStudentId.equals("")) {
					System.out.println("이미 로그인되어 있습니다.");
					continue;
				}

				sc.nextLine();

				System.out.print("아이디(dong)  : ");
				String loginId = sc.nextLine();

				System.out.print("비밀번호(dong) : ");
				String password = sc.nextLine();

				if (loginId.equals("")) {
					loginId = "dong";
				}
				if (password.equals("")) {
					password = "dong";
				}

				String studentId = accountRepository.login(loginId, password);
				if (studentId.equals("")) {
					System.out.println("아이디/비밀번호가 일치하지 않습니다.");
				}
				else {
					System.out.println("로그인에 성공했습니다.");
					loginedStudentId = studentId;
				}
			}
			else if (menu == 2) {
				System.out.println("로그아웃 했습니다.");
				loginedStudentId = "";
			}
			else if (menu == 3) {
				if (loginedStudentId.equals("")) {
					System.out.println("로그인을 먼저 해야합니다.");
					continue;
				}

				sc.nextLine();

				System.out.print("변경할 비밀번호 : ");
				String password = sc.nextLine();

				System.out.print("비밀번호 확인 : ");
				String passwordConfirm = sc.nextLine();

				if (password.equals("") || !password.equals(passwordConfirm)) {
					System.out.println("비밀번호가 비어있거나 일치하지 않습니다.");
					continue;
				}

				boolean success = accountRepository.changePassword(loginedStudentId, password);
				if (success) {
					System.out.println("비밀번호 변경을 성공했습니다. 로그아웃합니다.");
					loginedStudentId = "";
				}
				else {
					System.out.println("비밀번호 변경을 실패했습니다.");
				}

			}
			else if (menu == 4) {
				if (loginedStudentId.equals("")) {
					System.out.println("로그인을 먼저 해야합니다.");
					continue;
				}

				double result = accountRepository.getGPA(loginedStudentId);

				System.out.println("평균 학점은 " + result + "입니다.");

			}
		}
	}

	private void takeClassService() throws SQLException {
		while (true) {
			System.out.println();
			System.out.println("1. 이번 학기에 개설된 전체 분반 목록 보기");
			System.out.println("2. 수강 신청");
			System.out.println("3. 수강 취소");
			System.out.println("4. 특정 학과의 강의 목록 보기 (1번 쿼리)");
			System.out.println("0. 뒤로 가기");
			System.out.print("[수강신청 기능] 수행할 기능을 입력해주세요 : ");
			int menu = sc.nextInt();
			System.out.println();

			if (menu == 0) {
				break;
			}
			else if (menu == 1) {
				List<ClassInfo> classes = takeClassRepository.getClasses(2023, "2");

				System.out.println("이번 학기에 개설된 분반 목록입니다.");
				for(int i = 0; i < classes.size(); i += 10) {
					System.out.printf("%-11s | %-20s | 강의실 위치\n", "강의 코드", "교수명");
					for(int j = i; j < i + 10 && j < classes.size(); j++) {
						ClassInfo ci = classes.get(j);
						System.out.printf("%-11s | %-20s | %d-%s\n",
								ci.lectureCode + ci.sectionCode, ci.professorName,
								ci.buildingNumber, ci.roomCode);
					}
					System.out.printf("(%d/%d) Enter...\n", (i / 10) + 1, (classes.size() / 10) + 1);
					sc.nextLine();
				}
			}
			else if (menu == 2) {
				if (loginedStudentId.equals("")) {
					System.out.println("로그인을 먼저 해야합니다.");
					continue;
				}

				sc.nextLine();

				System.out.print("수강 신청할 전체 과목 코드를 입력해주세요 : ");
				String fullCode = sc.nextLine();
				String lectureCode = fullCode.substring(0, 8);
				String sectionCode = fullCode.substring(8);

				TakeClassResult result = takeClassRepository.takeClass(loginedStudentId, lectureCode, sectionCode);

				if (result.isSuccess) {
					System.out.println("수강 신청에 성공했습니다.");
				}
				else {
					System.out.println("수강 신청에 실패했습니다. 사유 : " + result.message);
				}
			}
			else if (menu == 3) {
				if (loginedStudentId.equals("")) {
					System.out.println("로그인을 먼저 해야합니다.");
					continue;
				}

				sc.nextLine();

				System.out.print("수강 신청할 전체 과목 코드를 입력해주세요 : ");
				String fullCode = sc.nextLine();
				String lectureCode = fullCode.substring(0, 8);
				String sectionCode = fullCode.substring(8);

				if (!takeClassRepository.takingClass(loginedStudentId, lectureCode, sectionCode)) {
					System.out.println("수강 신청된 과목이 아닙니다.");
				}
				else {
					boolean result = takeClassRepository.untakeClass(loginedStudentId, lectureCode, sectionCode);
					if (result) {
						System.out.println("수강 신청 취소에 성공했습니다.");
					}
					else {
						System.out.println("수강 신청 취소에 실패했습니다.");
					}
				}
			}
			else if (menu == 4) {
				sc.nextLine();

				System.out.print("조회할 학과 코드를 입력해주세요 : ");
				String departmentCode = sc.nextLine();

				Department department = departmentRepository.getDepartment(departmentCode);
				if (department == null) {
					System.out.println("해당 학과가 존재하지 않습니다.");
					continue;
				}
				List<Lecture> lectures = departmentRepository.getClassLectures(departmentCode);

				System.out.println(department.name + "의 강의 목록입니다.");
				for(int i = 0; i < lectures.size(); i += 10) {
					System.out.printf("%-11s | %-20s | 수강 학점\n", "강의 코드", "강의명");
					for(int j = i; j < i + 10 && j < lectures.size(); j++) {
						Lecture lecture = lectures.get(j);
						System.out.printf("%-11s | %-20s | %d\n", lecture.lectureCode, lecture.name, lecture.credits);
					}

					System.out.printf("(%d/%d) Enter...\n", (i / 10) + 1, (lectures.size() / 10) + 1);
					sc.nextLine();
				}
			}
		}
	}

	private void classService() throws SQLException {
		while (true) {
			System.out.println();
			System.out.println("1. 현재 수강 중인 수업 목록 보기 (4번 쿼리[쿼리 수정 필요])");
			System.out.println("2. 어느 한 수업의 게시글 목록 보기");
			System.out.println("3. 특정 게시글 보기");
			System.out.println("4. 특정 게시글의 댓글 목록 보기");
			System.out.println("5. 어느 한 수업의 수강생 목록 보기");
			System.out.println("6. 어느 한 수업의 수강생 수 보기 (5번 쿼리)");
			System.out.println("7. 특정 게시글의 댓글 목록 보기");
			System.out.println("8. 특정 요일에 진행되는 강의 목록 보기 (11번 쿼리)");
			System.out.println("0. 뒤로 가기");
			System.out.print("[수업 기능] 수행할 기능을 입력해주세요 : ");
			int menu = sc.nextInt();

			if (menu == 0) {
				break;
			}
		}
	}

	private void classroomService() throws SQLException {
		while (true) {
			System.out.println();
			System.out.println("1. 건물 번호 목록 조회");
			System.out.println("2. 특정 건물의 모든 강의실 목록 조회 (2번 쿼리)");
			System.out.println("0. 뒤로 가기");
			System.out.print("[강의실 기능] 수행할 기능을 입력해주세요 : ");
			int menu = sc.nextInt();

			if (menu == 0) {
				break;
			}
		}
	}

	private void departmentService() throws SQLException {
		while (true) {
			System.out.println();
			System.out.println("1. 특정 과의 학생 목록 조회(3번 쿼리)");
			System.out.println("2. 특정 과의 성적을 한번 이상 받은 학생 목록 조회 (9번 쿼리)");
			System.out.println("3. 특정 과의 교수의 수 조회(6번 쿼리)");
			System.out.println("4. 특정 과의 교수 목록 조회");
			System.out.println("5. 특정 학기에 수업을 진행하지 않는 특정 과의 교수 목록 조회 (10번 쿼리)");
			System.out.println("0. 뒤로 가기");
			System.out.print("[학과 기능] 수행할 기능을 입력해주세요 : ");
			int menu = sc.nextInt();
			System.out.println();

			sc.nextLine();

			System.out.print("조회할 학과 코드를 입력해주세요 : ");
			String departmentCode = sc.nextLine();

			Department department = departmentRepository.getDepartment(departmentCode);
			if (department == null) {
				System.out.println("해당 학과가 존재하지 않습니다.");
				continue;
			}

			if (menu == 0) {
				break;
			}
			else if (menu == 1) {
				List<Student> students = departmentRepository.getStudents(departmentCode);

				System.out.println(department.name + "의 학생 목록입니다.");
				printStudents(students);
			}
			else if (menu == 2) {
				List<Student> students = departmentRepository.getHavingGradeStudents(departmentCode);

				System.out.println(department.name + "의 성적을 한 개 이상 받은 학생 목록입니다.");
				printStudents(students);
			}
			else if (menu == 3) {
				int count = departmentRepository.countProfessors(departmentCode);
				System.out.printf("%s의 교수 수는 %d명입니다.\n", department.name, count);
			}
			else if (menu == 4) {
				List<Professor> professors = departmentRepository.getProfessors(departmentCode);

				System.out.println(department.name + "의 교수 목록입니다.");
				printProfessor(professors);
			}
			else if (menu == 5) {
				List<Professor> professors = departmentRepository.getNotTeachProfessors(departmentCode, 2023, "2");

				System.out.println(department.name + "의 수업을 진행하지 않는 교수 목록입니다.");
				printProfessor(professors);
			}
		}
	}

	private void printStudents(List<Student> students) {
		for(int i = 0; i < students.size(); i += 10) {
			System.out.printf("%-10s | %-20s | 학년\n", "학번", "이름");
			for(int j = i; j < i + 10 && j < students.size(); j++) {
				Student student = students.get(j);
				System.out.printf("%-11s | %-20s | %d\n", student.studentId, student.name, student.grade);
			}

			System.out.printf("(%d/%d) Enter...\n", (i / 10) + 1, (students.size() / 10) + 1);
			sc.nextLine();
		}
	}

	private void printProfessor(List<Professor> professors) {
		for(int i = 0; i < professors.size(); i += 10) {
			System.out.printf("%-10s | 이름\n", "교수 번호");
			for(int j = i; j < i + 10 && j < professors.size(); j++) {
				Professor professor = professors.get(j);
				System.out.printf("%-11s | %-20s\n", professor.professorId, professor.name);
			}

			System.out.printf("(%d/%d) Enter...\n", (i / 10) + 1, (professors.size() / 10) + 1);
			sc.nextLine();
		}
	}
}
