package knu.database.lms.controller;

import knu.database.lms.dto.Classroom;
import knu.database.lms.repositories.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
public class ClassroomController {
    private final ClassroomRepository classRoomRepository;

    // 1. 건물 번호 목록 조회
    @GetMapping("/building")
    public List<Classroom> getBuildingList(@SessionAttribute(name = "userId", required = false) String userId) throws SQLException {
        isLogin(userId);
        return classRoomRepository.getBuildings();
    }

    // 2. 특정 건물의 모든 강의실 목록 조회 (2번 쿼리)
    @GetMapping("/{buildingNumber}")
    public List<Classroom> getClassroomList(@SessionAttribute(name = "userId", required = false) String userId,
                                            @PathVariable int buildingNumber) throws SQLException {
        isLogin(userId);
        return classRoomRepository.getClassroomByBuilding(buildingNumber);
    }

    private void isLogin(String studentId) {
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }
}
