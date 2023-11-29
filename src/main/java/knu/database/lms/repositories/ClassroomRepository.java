package knu.database.lms.repositories;

import knu.database.lms.dto.Classroom;
import knu.database.lms.dto.ReserveClassroom;
import knu.database.lms.dto.ReserveClassroomResult;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClassroomRepository {
    private final DataSource dataSource;


    public ClassroomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Integer> getBuildingNumbers() throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "SELECT DISTINCT BUILDING_NUMBER FROM CLASSROOM ";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<Integer> buildingNumbers = new ArrayList<>();
        while(rs.next()) {
            buildingNumbers.add(rs.getInt(1));
        }

        rs.close();
        ps.close();
        conn.close();

        return buildingNumbers;
    }

    public List<Classroom> getClassroomByBuilding(int buildingNumber) throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "SELECT ROOM_CODE, NAME FROM CLASSROOM WHERE BUILDING_NUMBER = ? ";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, buildingNumber);
        ResultSet rs = ps.executeQuery();

        List<Classroom> classrooms = new ArrayList<>();
        while(rs.next()) {
            classrooms.add(new Classroom(
                    buildingNumber,
                    rs.getString(1),
                    rs.getString(2)
            ));
        }

        rs.close();
        ps.close();
        conn.close();

        return classrooms;
    }

    public ReserveClassroomResult reserveClassroom(String studentId, int buildingNumber, String roomCode,
                                                   LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        if (reservedClassroom(buildingNumber, roomCode, startDateTime, endDateTime)) {
            throw new SQLException("이미 예약된 강의실입니다.");
        }

        int seqNextVal;
        String sql = "SELECT reserved_classroom_seq.NEXTVAL FROM dual";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        seqNextVal = rs.getInt(1);
        rs.close();

        sql = "INSERT INTO RESERVED_CLASSROOM VALUES (?, ?, ?, ?, ?, ?) ";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, seqNextVal);
        ps.setString(2, studentId);
        ps.setInt(3, buildingNumber);
        ps.setString(4, roomCode);
        ps.setObject(5, Timestamp.valueOf(startDateTime));
        ps.setObject(6, Timestamp.valueOf(endDateTime));

        int result = ps.executeUpdate();

        if (result == 0) {
            return ReserveClassroomResult.failResult("알 수 없음.");
        }

        conn.commit();

        ps.close();
        conn.close();
        stmt.close();

        return ReserveClassroomResult.successResult();
    }

    public ReserveClassroomResult cancelClassroom(String studentId, int buildingNumber, String roomCode,
                                                   LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        String  sql = "DELETE FROM RESERVED_CLASSROOM " +
            "WHERE STUDENT_ID = ? AND BUILDING_NUMBER = ? AND ROOM_CODE = ? " +
            "AND START_TIMESTAMP = ? AND END_TIMESTAMP = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, studentId);
        ps.setInt(2, buildingNumber);
        ps.setString(3, roomCode);
        ps.setObject(4, Timestamp.valueOf(startDateTime));
        ps.setObject(5, Timestamp.valueOf(endDateTime));
        int result = ps.executeUpdate();

        if (result == 0) {
            return ReserveClassroomResult.failResult("알 수 없음.");
        }

        conn.commit();

        ps.close();
        conn.close();

        return ReserveClassroomResult.successResult();
    }

    public List<ReserveClassroom> getReservedClassrooms(String studentId) throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "SELECT BUILDING_NUMBER, ROOM_CODE, START_TIMESTAMP, END_TIMESTAMP " +
                "FROM RESERVED_CLASSROOM " +
                "WHERE STUDENT_ID = ? ";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, studentId);
        ResultSet rs = ps.executeQuery();

        List<ReserveClassroom> reserveClassrooms = new ArrayList<>();
        while(rs.next()) {
            reserveClassrooms.add(new ReserveClassroom(
                rs.getInt(1),
                rs.getString(2),
                rs.getTimestamp(3).toLocalDateTime(),
                rs.getTimestamp(4).toLocalDateTime()
            ));
        }

        rs.close();
        ps.close();
        conn.close();

        return reserveClassrooms;
    }

    private boolean reservedClassroom(int buildingNumber, String roomCode,
                                      LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException {
        Connection conn = dataSource.getConnection();
        String sql = "SELECT * FROM RESERVED_CLASSROOM " +
            "WHERE BUILDING_NUMBER = ? AND ROOM_CODE = ? " +
            "AND ((? BETWEEN START_TIMESTAMP AND END_TIMESTAMP) OR " +
            "(? BETWEEN START_TIMESTAMP AND END_TIMESTAMP))";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, buildingNumber);
        ps.setString(2, roomCode);
        ps.setObject(3, Timestamp.valueOf(startDateTime));
        ps.setObject(4, Timestamp.valueOf(endDateTime));

        ResultSet rs = ps.executeQuery();
        boolean result = rs.next();

        rs.close();
        ps.close();

        return result;
    }
}
