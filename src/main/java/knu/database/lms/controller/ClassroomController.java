package knu.database.lms.controller;

import knu.database.lms.dto.Classroom;
import knu.database.lms.repositories.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClassroomController {
    private final ClassroomRepository classRoomRepository;

    @GetMapping("/classroom")
    public ModelAndView classroomPage(
            @RequestParam(name = "buildingNumber", required = false) Integer buildingNumber,
            @SessionAttribute(name = "userId", required = false) String userId) throws SQLException {
        ModelAndView mav = new ModelAndView();

        if (userId == null) {
            mav.setViewName("redirect:/");
        }
        else {
            mav.setViewName("classroom");
        }

        if (buildingNumber == null) {
            List<Integer> buildingNumbers = classRoomRepository.getBuildingNumbers();

            mav.addObject("buildingNumbers", buildingNumbers);
        }
        else {
            List<Classroom> classrooms = classRoomRepository.getClassroomByBuilding(buildingNumber);

            mav.addObject("classrooms", classrooms);
        }

        return mav;
    }


    // 2. 특정 건물의 모든 강의실 목록 조회 (2번 쿼리)
    @GetMapping("/api/classroom/{buildingNumber}")
    @ResponseBody
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
