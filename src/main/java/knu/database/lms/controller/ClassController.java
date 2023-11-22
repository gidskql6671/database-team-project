package knu.database.lms.controller;

import knu.database.lms.dto.*;
import knu.database.lms.repositories.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ClassController {
    private final ClassRepository classRepository;

    // 1. 현재 수강 중인 수업 목록 보기 (4번 쿼리)
    @GetMapping("/list")
    public List<ClassInfo> getClassList(@SessionAttribute(name = "userId", required = false) Long userId) throws SQLException {
       return classRepository.getTakingClass(userId.toString());
    }

    // 2. 수강 중인 수업의 게시글 목록 보기
    @PostMapping("/post/list")
    public List<Post> getClassPostList(@SessionAttribute(name = "userId", required = false) Long userId, @RequestBody PostRequestDto postRequestDto) throws SQLException {
        isTakingClass(userId.toString(), postRequestDto.getLectureCode(), postRequestDto.getSectionCode());
        return classRepository.getPosts(postRequestDto.getLectureCode(), postRequestDto.getSectionCode());
    }

    // 3. 수강 중인 수업의 게시글 보기
    @PostMapping("/post")
    public Post getClassPost(@SessionAttribute(name = "userId", required = false) Long userId, @RequestBody PostRequestDto postRequestDto) throws SQLException {
        isTakingClass(userId.toString(), postRequestDto.getLectureCode(), postRequestDto.getSectionCode());
        return classRepository.getPost(postRequestDto.getLectureCode(), postRequestDto.getSectionCode(), postRequestDto.getPostId());
    }

    // 4. 수강 중인 수업의 게시글의 댓글 목록 보기
    @PostMapping("/post/comment/list")
    public List<Comment> getCommentList(@SessionAttribute(name = "userId", required = false) Long userId, @RequestBody PostRequestDto postRequestDto) throws SQLException {
        isTakingClass(userId.toString(), postRequestDto.getLectureCode(), postRequestDto.getSectionCode());
        return classRepository.getComments(postRequestDto.getLectureCode(), postRequestDto.getSectionCode(), postRequestDto.getPostId());
    }

    // 5. 어느 한 수업의 수강생 목록 보기
    @PostMapping("/student/list")
    public List<String> getTakingClassStudentList(@SessionAttribute(name = "userId", required = false) Long userId, @RequestBody ClassRequestDto classRequestDto) throws SQLException {
        isTakingClass(userId.toString(), classRequestDto.getLectureCode(), classRequestDto.getSectionCode());
        return classRepository.getStudents(classRequestDto.getLectureCode(), classRequestDto.getSectionCode());
    }

    // 6. 어느 한 수업의 수강생 수 보기 (5번 쿼리)
    @PostMapping("/student/count")
    public int getTakingClassStudentCount(@SessionAttribute(name = "userId", required = false) Long userId, @RequestBody ClassRequestDto classRequestDto) throws SQLException {
        isTakingClass(userId.toString(), classRequestDto.getLectureCode(), classRequestDto.getSectionCode());
        return classRepository.getStudentsCount(classRequestDto.getLectureCode(), classRequestDto.getSectionCode());
    }

    // 7. 특정 요일에 진행되는 강의 목록 보기 (12번 쿼리)
    @PostMapping("/day/list")
    public List<ClassInfo> getClassListByDayOfWeek(@SessionAttribute(name = "userId", required = false) Long userId, @RequestBody DayOfWeekRequestDto dayOfWeekRequestDto) throws SQLException {
        return classRepository.getClassByDayOfWeek(dayOfWeekRequestDto.getDayOfWeek());
    }

    // 8. 수강 중인 수업의 게시글에 댓글 작성
    @PostMapping("/post/comment")
    public void writeComment(@SessionAttribute(name = "userId", required = false) Long userId, @RequestBody CreateCommentRequestDto commentRequestDto) throws SQLException {
        isTakingClass(userId.toString(), commentRequestDto.getLectureCode(), commentRequestDto.getSectionCode());
        classRepository.writeComment(commentRequestDto.getPostId(), commentRequestDto.getContent(), userId.toString());
    }

    private void isTakingClass(String studentId, String lectureCode, String sectionCode) throws SQLException {
        boolean isTakingClass = classRepository.isTakingClass(studentId, lectureCode, sectionCode);
        if (!isTakingClass) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수강하지 않는 과목입니다.");
        }
    }
}
