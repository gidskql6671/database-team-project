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

}
