package knu.database.lms.controller;

import knu.database.lms.dto.Student;
import knu.database.lms.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;


@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;

    // 1. 특정 과의 학생 목록 조회(3번 쿼리)
    @GetMapping("/{departmentCode}/student")
    public List<Student> getStudentList(@SessionAttribute(name = "userId", required = false) String userId,
                                        @PathVariable String departmentCode) throws SQLException {
        isLogin(userId);
        return departmentRepository.getStudents(departmentCode);
    }


    private void isLogin(String studentId) {
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }
}
