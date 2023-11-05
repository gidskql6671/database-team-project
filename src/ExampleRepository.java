import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

public class ExampleRepository {
// 참고용
	public static void doTask1(Connection conn, Statement stmt) {
		String sql = ""; // an SQL statement

		try {
			int res = 0;

			sql = "CREATE TABLE DEPARTMENT(" +
					"    Dname           varchar(15) not null," +
					"    Dnumber         number      not null," +
					"    Mgr_ssn         char(9)     default '888665555' not null," +
					"    Mgr_start_date  date," +
					"    PRIMARY KEY (Dnumber)," +
					"    UNIQUE (Dname)" +
					")";
			stmt.addBatch(sql);

			sql = "CREATE TABLE EMPLOYEE(" +
					"    Fname           varchar(15)     not null," +
					"    Minit           char," +
					"    Lname           varchar(15)," +
					"    Ssn             char(9)         not null," +
					"    Bdate           date," +
					"    Address         varchar(30)," +
					"    Sex             char," +
					"    Salary          number(10, 2)," +
					"    Super_ssn       char(9)," +
					"    Dno             number          default 1 not null," +
					"    PRIMARY KEY (Ssn)," +
					"    FOREIGN KEY (Dno) REFERENCES DEPARTMENT(Dnumber) ON DELETE SET NULL" +
					")";
			stmt.addBatch(sql);

			sql = "CREATE TABLE DEPT_LOCATIONS(" +
					"    Dnumber         number      not null," +
					"    Dlocation       varchar(15) not null," +
					"    PRIMARY KEY (Dnumber, Dlocation)," +
					"    FOREIGN KEY (Dnumber) REFERENCES DEPARTMENT(Dnumber) ON DELETE CASCADE" +
					")";
			stmt.addBatch(sql);

			sql = "CREATE TABLE PROJECT(" +
					"    Pname           varchar(15) not null," +
					"    Pnumber         number      not null," +
					"    Plocation       varchar(15)," +
					"    Dnum            number      not null," +
					"    PRIMARY KEY (Pnumber)," +
					"    UNIQUE (Pname)," +
					"    FOREIGN KEY (Dnum) REFERENCES DEPARTMENT(Dnumber)" +
					")";
			stmt.addBatch(sql);

			sql = "CREATE TABLE WORKS_ON(" +
					"    Essn            char(9)     not null," +
					"    Pno             number      not null," +
					"    Hours           number(3, 1)," +
					"    PRIMARY KEY (Essn, Pno)," +
					"    FOREIGN KEY (Essn) REFERENCES EMPLOYEE(Ssn)," +
					"    FOREIGN KEY (Pno) REFERENCES PROJECT(Pnumber)" +
					")";
			stmt.addBatch(sql);

			sql = "CREATE TABLE DEPENDENT(" +
					"    Essn            char(9)     not null," +
					"    Dependent_name  varchar(15) not null," +
					"    Sex             char," +
					"    Bdate           date," +
					"    Relationship    varchar(8)," +
					"    PRIMARY KEY (Essn, Dependent_name)," +
					"    FOREIGN KEY (Essn) REFERENCES EMPLOYEE(Ssn)" +
					")";
			stmt.addBatch(sql);

			stmt.executeBatch();

			conn.commit();

			System.out.println("DB Create Success!");

			Scanner scanner = new Scanner(new File("company.txt"));

			while (scanner.hasNextLine()) {
				String table = scanner.nextLine().substring(1);
				String[] attrs = scanner.nextLine().split("#");

				sql = "INSERT INTO " + table + " VALUES ";

				switch (table) {
					case "DEPARTMENT":
						sql += String.format("('%s', %s, '%s', to_date('%s', 'YYYY-MM-DD'))", attrs[0], attrs[1], attrs[2], attrs[3]);

						break;
					case "EMPLOYEE":
						sql += String.format("('%s', '%s', '%s', '%s', to_date('%s', 'YYYY-MM-DD'), '%s', '%s', %s, '%s', %s)",
								attrs[0], attrs[1], attrs[2], attrs[3], attrs[4], attrs[5], attrs[6], attrs[7], attrs[8], attrs[9]);

						break;
					case "DEPT_LOCATIONS":
						sql += String.format("(%s, '%s')", attrs[0], attrs[1]);

						break;
					case "PROJECT":
						sql += String.format("('%s', %s, '%s', %s)", attrs[0], attrs[1], attrs[2], attrs[3]);

						break;
					case "WORKS_ON":
						sql += String.format("('%s', %s, %s)", attrs[0], attrs[1], attrs[2]);

						break;
					case "DEPENDENT":
						sql += String.format("('%s', '%s', '%s', to_date('%s', 'YYYY-MM-DD'), '%s')", attrs[0], attrs[1], attrs[2], attrs[3], attrs[4]);

						break;
					default:
						System.err.println("잘못된 파일 입력값");
						System.exit(1);
				}

				sql = sql.replace("'NULL'", "NULL");
				stmt.addBatch(sql);
			}

			stmt.executeBatch();

			conn.commit();
			System.out.println("Insert Success!");

			sql = "ALTER TABLE EMPLOYEE ADD FOREIGN KEY (Super_ssn) REFERENCES EMPLOYEE(Ssn) ON DELETE SET NULL";
			stmt.executeUpdate(sql);
			sql = "ALTER TABLE DEPARTMENT ADD FOREIGN KEY (Mgr_ssn) REFERENCES EMPLOYEE(Ssn) ON DELETE SET NULL";
			stmt.executeUpdate(sql);
			conn.commit();

			System.out.println("Alter Success!");

		}catch(SQLException ex2) {
			System.err.println("sql error = " + ex2.getMessage());
			System.exit(1);
		}catch (FileNotFoundException e) {
			System.err.println("Input file not found");
			System.exit(1);
		}
	}

	public static void doTask2(Connection conn, Statement stmt) {

		ResultSet rs = null;
		try {
			// Q1: Complete your query.
			String sql =
					"SELECT E.SEX, AVG(E.SALARY) FROM EMPLOYEE E " +
							"WHERE EXISTS(SELECT * FROM DEPENDENT D WHERE D.ESSN = E.SSN) " +
							"GROUP BY E.SEX " +
							"ORDER BY AVG(E.SALARY) DESC";
			rs = stmt.executeQuery(sql);
			System.out.println("<< query 1 result >>");
			System.out.printf("%-10s | %-10s\n", "Gender", "Avg_Sal");
			System.out.println("-----------------------");
			while(rs.next()) {
				String sex = rs.getString(1);
				double avg = rs.getDouble(2);
				System.out.printf("%-10s | %-10.2f\n", sex, avg);
			}
			rs.close();

			System.out.println();

			// Q2: Complete your query.
			sql = "SELECT E.FNAME, E.LNAME, E.ADDRESS, SE.FNAME, SE.LNAME " +
					"FROM EMPLOYEE E JOIN EMPLOYEE SE ON E.SUPER_SSN = SE.SSN " +
					"WHERE NOT EXISTS(" +
					"(SELECT P.PNUMBER FROM PROJECT P WHERE P.DNUM = 1) " +
					"MINUS " +
					"(SELECT W.PNO FROM WORKS_ON W WHERE W.ESSN = E.SSN)" +
					") " +
					"ORDER BY E.ADDRESS";
			rs = stmt.executeQuery(sql);
			System.out.println("<< query 2 result >>");
			System.out.printf("%-10s | %-10s | %-25s | %-11s | %-10s\n",
					"FName", "LName", "E_Address", "Super_FName", "Super_LName");
			System.out.println("--------------------------------------------------------------------------");
			while(rs.next()) {
				String fName = rs.getString(1);
				String lName = rs.getString(2);
				String address = rs.getString(3);
				String sFName = rs.getString(4);
				String sLName = rs.getString(5);
				System.out.printf("%-10s | %-10s | %-25s | %-11s | %-10s\n",
						fName, lName, address, sFName, sLName);
			}
			rs.close();

			System.out.println();

			// Q3: Complete your query.
			sql = "SELECT D.DNAME, P.PNAME, E.LNAME, E.FNAME, E.SALARY " +
					"FROM (EMPLOYEE E FULL OUTER JOIN DEPARTMENT D ON E.DNO = D.DNUMBER) FULL OUTER JOIN PROJECT P ON D.DNUMBER = P.DNUM " +
					"WHERE P.PLOCATION = 'Houston' " +
					"ORDER BY D.DNAME, E.SALARY DESC";
			rs = stmt.executeQuery(sql);
			System.out.println("<< query 3 result >>");
			System.out.printf("%-15s | %-20s | %-10s | %-10s | %-10s\n",
					"DName", "PName", "LName", "FName", "Salary");
			System.out.println("--------------------------------------------------------------------------");
			while(rs.next()) {
				String dName = rs.getString(1);
				String pName = rs.getString(2);
				String lName = rs.getString(3);
				String fName = rs.getString(4);
				double salary = rs.getDouble(5);
				System.out.printf("%-15s | %-20s | %-10s | %-10s | %-10f\n",
						dName, pName, lName, fName, salary);
			}

			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

