package knu.database.lms.controller;

import knu.database.lms.dto.Classroom;
import knu.database.lms.repositories.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
public class ClassroomController {
    private final ClassroomRepository classRoomRepository;

    // 1. 건물 번호 목록 조회
    @GetMapping("/building")
    public List<Classroom> getBuildingList() throws SQLException {
        return classRoomRepository.getBuildings();
    }

    // 2. 특정 건물의 모든 강의실 목록 조회 (2번 쿼리)
    @GetMapping("/{buildingNumber}")
    public List<Classroom> getClassroomList(@PathVariable int buildingNumber) throws SQLException {
        return classRoomRepository.getClassroomByBuilding(buildingNumber);
    }
}
