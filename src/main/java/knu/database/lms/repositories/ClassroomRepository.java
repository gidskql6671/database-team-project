package knu.database.lms.repositories;

import knu.database.lms.dto.Classroom;
import knu.database.lms.dto.ReserveClassroom;
import knu.database.lms.dto.ReserveClassroomResult;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
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
        LocalDateTime now = LocalDateTime.now();

        if (endDateTime.isBefore(now) || startDateTime.isBefore(now)) {
            return ReserveClassroomResult.failResult("현재보다 이전 날짜로 예약할 수 없습니다.");
        }

        if (startDateTime.isAfter(endDateTime)) {
            return ReserveClassroomResult.failResult("시작 시간이 종료 시간 보다 늦습니다.");
        }

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            if (reservedClassroom(buildingNumber, roomCode, startDateTime, endDateTime)) {
                throw new SQLException("이미 예약된 강의실입니다.");
            }

            String sql =
                    "INSERT INTO RESERVED_CLASSROOM VALUES (reserved_classroom_seq.NEXTVAL, ?, ?, ?, ?, ?) ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setInt(2, buildingNumber);
            ps.setString(3, roomCode);
            ps.setObject(4, Timestamp.valueOf(startDateTime));
            ps.setObject(5, Timestamp.valueOf(endDateTime));

            int result = ps.executeUpdate();

            if (result == 0) {
                throw new SQLException("알 수 없음.");
            }

            conn.commit();

            ps.close();
            conn.close();
            stmt.close();

            return ReserveClassroomResult.successResult();
        }
        catch (Exception e) {
            assert conn != null;
            conn.rollback();
            conn.close();

            return ReserveClassroomResult.failResult(e.getMessage());
        }
    }

    public ReserveClassroomResult cancelClassroom(String studentId, int reservedId) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            String sql = "DELETE FROM RESERVED_CLASSROOM WHERE STUDENT_ID = ? AND RESERVED_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setInt(2, reservedId);
            int result = ps.executeUpdate();

            if (result == 0) {
                throw new Exception("알 수 없음.");
            }

            conn.commit();

            ps.close();
            conn.close();

            return ReserveClassroomResult.successResult();}
        catch (Exception e) {
            assert conn != null;
            conn.rollback();
            conn.close();

            return ReserveClassroomResult.failResult(e.getMessage());
        }
    }

    public List<ReserveClassroom> getReservedClassrooms(String studentId) throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "SELECT RESERVED_ID, BUILDING_NUMBER, ROOM_CODE, START_TIMESTAMP, END_TIMESTAMP " +
                "FROM RESERVED_CLASSROOM " +
                "WHERE STUDENT_ID = ? " +
                "ORDER BY START_TIMESTAMP, END_TIMESTAMP";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, studentId);
        ResultSet rs = ps.executeQuery();

        List<ReserveClassroom> reserveClassrooms = new ArrayList<>();
        while(rs.next()) {
            reserveClassrooms.add(new ReserveClassroom(
                    rs.getInt(1),
                    rs.getInt(2),
                rs.getString(3),
                rs.getTimestamp(4).toLocalDateTime(),
                rs.getTimestamp(5).toLocalDateTime()
            ));
        }

        rs.close();
        ps.close();
        conn.close();

        return reserveClassrooms;
    }

    public List<int[]> getClassRoomAvailable(int buildingNumber, String roomCode, LocalDate date) throws SQLException{
        Connection conn = dataSource.getConnection();
        String sql = "SELECT START_TIMESTAMP, END_TIMESTAMP " +
                "FROM RESERVED_CLASSROOM " +
                "WHERE START_TIMESTAMP >= ? AND END_TIMESTAMP <= ? " +
                "AND BUILDING_NUMBER = ? AND ROOM_CODE = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1, Timestamp.valueOf(date.atStartOfDay()));
        ps.setObject(2, Timestamp.valueOf(date.atTime(23, 59, 59)));
        ps.setInt(3, buildingNumber);
        ps.setString(4, roomCode);

        ResultSet rs = ps.executeQuery();

        return getAvailableTimeSlots(rs);
    }

    public boolean existsClassroom(int buildingNumber, String roomCode) throws SQLException {
        Connection conn = dataSource.getConnection();
        String sql = "SELECT * FROM CLASSROOM WHERE BUILDING_NUMBER = ? AND ROOM_CODE = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, buildingNumber);
        ps.setString(2, roomCode);

        ResultSet rs = ps.executeQuery();

        boolean result = rs.next();

        rs.close();
        ps.close();
        conn.close();

        return result;
    }

    private boolean reservedClassroom(int buildingNumber, String roomCode,
                                      LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException {
        Connection conn = dataSource.getConnection();
        String sql = "SELECT * FROM RESERVED_CLASSROOM " +
            "WHERE BUILDING_NUMBER = ? AND ROOM_CODE = ? " +
            "AND START_TIMESTAMP < ? AND END_TIMESTAMP > ? ";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, buildingNumber);
        ps.setString(2, roomCode);
        ps.setObject(3, Timestamp.valueOf(endDateTime));
        ps.setObject(4, Timestamp.valueOf(startDateTime));

        ResultSet rs = ps.executeQuery();
        boolean result = rs.next();

        rs.close();
        ps.close();

        return result;
    }

    private List<int[]> getAvailableTimeSlots(ResultSet rs) throws SQLException {
        int startTime = 8;
        int endTime = 22;
        boolean[] isReserved = new boolean[24];

        while (rs.next()) {
            LocalDateTime startTimestamp = rs.getTimestamp(1).toLocalDateTime();
            LocalDateTime endTimestamp = rs.getTimestamp(2).toLocalDateTime();

            int startHour = startTimestamp.getHour();
            int endHour = endTimestamp.getHour();

            for (int i = startHour; i < endHour; i++) {
                isReserved[i] = true;
            }
        }

        List<int[]> availableTimeSlots = new ArrayList<>();
        int startHour = -1;

        for (int hour = startTime; hour < endTime; hour++) {
            if (!isReserved[hour]) {
                if (startHour == -1) {
                    startHour = hour;
                }
            } else {
                if (startHour != -1) {
                    int endHour = hour;
                    availableTimeSlots.add(new int[]{startHour, endHour});
                    startHour = -1;
                }
            }
        }

        // 마지막 예약된 시간대 처리
        if (startHour != -1) {
            availableTimeSlots.add(new int[]{startHour, endTime});
        }

        return availableTimeSlots;
    }
}
