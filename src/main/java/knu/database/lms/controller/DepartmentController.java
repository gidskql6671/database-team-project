package knu.database.lms.controller;

import knu.database.lms.dto.Professor;
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

    // 2. 특정 과의 성적을 한번 이상 받은 학생 목록 조회 (9번 쿼리)
    @GetMapping("/{departmentCode}/student/graded")
    public List<Student> getStudentGradeList(@SessionAttribute(name = "userId", required = false) String userId,
                                             @PathVariable String departmentCode) throws SQLException {
        isLogin(userId);
        return departmentRepository.getHavingGradeStudents(departmentCode);
    }

    // 3. 특정 과의 교수의 수 조회(6번 쿼리)
    @GetMapping("/{departmentCode}/professor/count")
    public int getProfessorCount(@SessionAttribute(name = "userId", required = false) String userId,
                                 @PathVariable String departmentCode) throws SQLException {
        isLogin(userId);
        return departmentRepository.countProfessors(departmentCode);
    }

    // 4. 특정 과의 교수 목록 조회
    @GetMapping("/{departmentCode}/professor")
    public List<Professor> getProfessorList(@SessionAttribute(name = "userId", required = false) String userId,
                                            @PathVariable String departmentCode) throws SQLException {
        isLogin(userId);
        return departmentRepository.getProfessors(departmentCode);
    }

    // 5. 특정 학기에 수업을 진행하지 않는 특정 과의 교수 목록 조회 (10번 쿼리)
    @GetMapping("/{departmentCode}/professor/notTeach")
    public List<Professor> getProfessorNotTeachingList(@SessionAttribute(name = "userId", required = false) String userId,
                                                       @PathVariable String departmentCode,
                                                       @RequestParam int year,
                                                       @RequestParam String semester) throws SQLException {
        isLogin(userId);
        return departmentRepository.getNotTeachProfessors(departmentCode, year, semester);
    }

    private void isLogin(String studentId) {
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }
}
