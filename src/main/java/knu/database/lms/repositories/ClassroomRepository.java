package knu.database.lms.repositories;

import knu.database.lms.dto.Classroom;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClassroomRepository {
    private final DataSource dataSource;


    public ClassroomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Classroom> getBuildings() throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "SELECT DISTINCT BUILDING_NUMBER FROM CLASSROOM ";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<Classroom> building = new ArrayList<>();
        while(rs.next()) {
            building.add(new Classroom(
                    rs.getInt(1)
            ));
        }

        rs.close();
        ps.close();
        conn.close();

        return building;
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
                    rs.getString(1)
            ));
        }

        rs.close();
        ps.close();
        conn.close();

        return classrooms;
    }



}
