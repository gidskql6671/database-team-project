import dto.ClassInfo;
import dto.Post;
import repositories.AccountRepository;
import repositories.ClassRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleProgram {
	public static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
	public static final String USER_UNIVERSITY ="lms";
	public static final String USER_PASSWD ="lms";

	Scanner sc;
	private String loginedStudentId = "";
	private AccountRepository accountRepository;
	private ClassRepository classRepository;


	public ConsoleProgram() throws SQLException {}

	public void run() throws SQLException {
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

		conn.setAutoCommit(false); // auto-commit disabled
		stmt = conn.createStatement();

		sc = new Scanner(System.in);

		// 레포지토리 객체 생성
		accountRepository = new AccountRepository(conn, stmt);
		classRepository = new ClassRepository(conn, stmt);


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

		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void accountService() {
		while (true) {
			System.out.println();
			System.out.println("1. 로그인 (아이디 : dong, 비밀번호 : dong)");
			System.out.println("2. 로그아웃");
			System.out.println("3. 비밀번호 변경");
			System.out.println("4. 평균 평점을 조회한다. (18번쿼리)");
			System.out.println("0. 뒤로 가기");
			System.out.print("[회원 기능] 수행할 기능을 입력해주세요 : ");
			int menu = sc.nextInt();

			if (menu == 0) {
				break;
			}
			else if (menu == 1) {
				if (!loginedStudentId.equals("")) {
					System.out.println("이미 로그인되어 있습니다.");
					continue;
				}

				System.out.println();
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

				System.out.println();
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

	private void takeClassService() {
		while (true) {
			System.out.println();
			System.out.println("1. 이번 학기에 개설된 전체 분반 목록 보기");
			System.out.println("2. 수강 신청");
			System.out.println("3. 수강 취소");
			System.out.println("4. 특정 학과의 강의 목록 보기 (1번 쿼리)");
			System.out.println("0. 뒤로 가기");
			System.out.print("[수강신청 기능] 수행할 기능을 입력해주세요 : ");
			int menu = sc.nextInt();

			if (menu == 0) {
				break;
			}
		}
	}

	private void classService() {
		while (true) {
			System.out.println();
			System.out.println("1. 현재 수강 중인 수업 목록 보기 (4번 쿼리)");
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
			else if (menu == 1) {
				if (loginedStudentId.equals("")) {
					System.out.println("로그인을 먼저 해야합니다.");
					continue;
				}

				List<ClassInfo> takingClasses = classRepository.getTakingClass(loginedStudentId);

				System.out.print("현재 수강중인 과목은 ");
				if (takingClasses.isEmpty()) {
					System.out.println("없습니다.");
				}
				else {
					System.out.printf("'%s'(%s%s)",
							takingClasses.get(0).name,
							takingClasses.get(0).lectureCode,
							takingClasses.get(0).sectionCode);
					for(int i = 1; i < takingClasses.size(); i++) {
						System.out.printf(", '%s'(%s%s)",
								takingClasses.get(i).name,
								takingClasses.get(i).lectureCode,
								takingClasses.get(i).sectionCode);
					}
					System.out.println("입니다.");
				}
			}
			else if (menu == 2) {
				if (loginedStudentId.equals("")) {
					System.out.println("로그인을 먼저 해야합니다.");
					continue;
				}

				System.out.println();
				sc.nextLine();

				System.out.print("전체 과목 코드를 입력해주세요 : ");
				String fullCode = sc.nextLine();
				String lectureCode = fullCode.substring(0, 8);
				String sectionCode = fullCode.substring(8);

				List<Post> posts = classRepository.getPosts(loginedStudentId, lectureCode, sectionCode);

				if (posts == null) {
					System.out.println("수강하지 않는 과목입니다.");
					continue;
				}

				System.out.println("해당 과목의 게시글들은 다음과 같습니다.");
				System.out.printf("%-9s | %-20s | %-20s\n", "게시글 ID", "제목", "작성자명");
				for (Post post : posts) {
					System.out.printf("%-9d | %-20s | %-20s\n",
							post.id, post.title, post.publisherName);
				}
			}
		}
	}

	private void classroomService() {
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

	private void departmentService() {
		while (true) {
			System.out.println();
			System.out.println("1. 특정 과의 학생 목록 조회(3번 쿼리[쿼리 수정 필요])");
			System.out.println("2. 성적을 한번 이상 받은 학생 목록 조회 (9번 쿼리)");
			System.out.println("3. 특정 과의 교수 목록 조회(6번 쿼리[쿼리 수정 필요])");
			System.out.println("4. 특정 학기에 수업을 진행하지 않는 특정 과의 교수 목록 조회 (10번 쿼리[쿼리 수정 필요])");
			System.out.println("0. 뒤로 가기");
			System.out.print("[학과 기능] 수행할 기능을 입력해주세요 : ");
			int menu = sc.nextInt();

			if (menu == 0) {
				break;
			}
		}
	}
}
