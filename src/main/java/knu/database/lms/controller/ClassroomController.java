package knu.database.lms.controller;

import knu.database.lms.dto.Classroom;
import knu.database.lms.dto.ReserveClassroom;
import knu.database.lms.dto.ReserveClassroomResult;
import knu.database.lms.dto.controller.CancelClassroomRequestDto;
import knu.database.lms.dto.controller.ClassRoomAvailableRequest;
import knu.database.lms.dto.controller.ReserveClassroomRequestDto;
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

        List<ReserveClassroom> reservedClassrooms = classRoomRepository.getReservedClassrooms(userId);

        mav.addObject("reservedClassrooms", reservedClassrooms);

        return mav;
    }

    @ResponseBody
    @PostMapping("/api/classroom")
    public void reserveClassroom(@RequestBody ReserveClassroomRequestDto reserveClassroomRequestDto,
                                 @SessionAttribute(name = "userId", required = false) String userId) throws SQLException {
        isLogin(userId);
        ReserveClassroomResult result = classRoomRepository.reserveClassroom(userId, reserveClassroomRequestDto.getBuildingNumber(), reserveClassroomRequestDto.getRoomCode(),
                reserveClassroomRequestDto.getStartDateTime(), reserveClassroomRequestDto.getEndDateTime());

        if (!result.isSuccess) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.message);
        }
    }

    @ResponseBody
    @DeleteMapping("/api/classroom")
    public void cancelClassroom(@RequestBody CancelClassroomRequestDto req,
                                @SessionAttribute(name = "userId", required = false) String userId) throws SQLException {
        isLogin(userId);
        ReserveClassroomResult result = classRoomRepository.cancelClassroom(userId, req.getReservedId());

        if (!result.isSuccess) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.message);
        }
    }

    @ResponseBody
    @GetMapping("/api/classroom")
    public List<ReserveClassroom> getReservedClassrooms(@SessionAttribute(name = "userId", required = false) String userId) throws SQLException {
        isLogin(userId);
        return classRoomRepository.getReservedClassrooms(userId);
    }

    @ResponseBody
    @GetMapping("/api/classroom/available")
    public List<int[]> getAvailableClassrooms(@SessionAttribute(name = "userId", required = false) String userId,
                                              @ModelAttribute ClassRoomAvailableRequest request) throws SQLException {
        isLogin(userId);

        int buildingNumber = request.getBuildingNumber();
        String roomCode = request.getRoomCode();

        if (!classRoomRepository.existsClassroom(buildingNumber, roomCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "강의실이 존재하지 않습니다.");
        }

        return classRoomRepository.getClassRoomAvailable(buildingNumber, roomCode, request.getDate());
    }

    private void isLogin(String studentId) {
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }


}
