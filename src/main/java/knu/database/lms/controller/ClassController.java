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
    @GetMapping
    public List<ClassInfo> getClassList(@SessionAttribute(name = "userId", required = false) String userId) throws SQLException {
        isLogin(userId);
       return classRepository.getTakingClass(userId);
    }

    // 2. 수강 중인 수업의 게시글 목록 보기
    @GetMapping("/{lectureCode}/{sectionCode}/post")
    public List<Post> getClassPostList(@SessionAttribute(name = "userId", required = false) String userId,
                                       @PathVariable String lectureCode, @PathVariable String sectionCode) throws SQLException {
        isLogin(userId);
        isTakingClass(userId, lectureCode, sectionCode);
        return classRepository.getPosts(lectureCode, sectionCode);
    }

    // 3. 수강 중인 수업의 게시글 보기
    @GetMapping("/{lectureCode}/{sectionCode}/post/{postId}")
    public Post getClassPost(@SessionAttribute(name = "userId", required = false) String userId,
                             @PathVariable String lectureCode, @PathVariable String sectionCode,
                             @PathVariable int postId) throws SQLException {
        isLogin(userId);
        isTakingClass(userId, lectureCode, sectionCode);
        return classRepository.getPost(lectureCode, sectionCode, postId);
    }

    // 4. 수강 중인 수업의 게시글의 댓글 목록 보기
    @GetMapping("/{lectureCode}/{sectionCode}/post/{postId}/comment")
    public List<Comment> getCommentList(@SessionAttribute(name = "userId", required = false) String userId,
                                        @PathVariable String lectureCode, @PathVariable String sectionCode,
                                        @PathVariable int postId) throws SQLException {
        isLogin(userId);
        isTakingClass(userId, lectureCode, sectionCode);
        return classRepository.getComments(lectureCode, sectionCode, postId);
    }

    // 5. 어느 한 수업의 수강생 목록 보기
    @GetMapping("/{lectureCode}/{sectionCode}/students")
    public List<String> getTakingClassStudentList(@SessionAttribute(name = "userId", required = false) String userId,
                                                  @PathVariable String lectureCode, @PathVariable String sectionCode) throws SQLException {
        isLogin(userId);
        isTakingClass(userId, lectureCode, sectionCode);
        return classRepository.getStudents(lectureCode, sectionCode);
    }

    // 6. 어느 한 수업의 수강생 수 보기 (5번 쿼리)
    @GetMapping("/{lectureCode}/{sectionCode}/students/count")
    public int getTakingClassStudentCount(@SessionAttribute(name = "userId", required = false) String userId,
                                          @PathVariable String lectureCode, @PathVariable String sectionCode) throws SQLException {
        isLogin(userId);
        isTakingClass(userId, lectureCode, sectionCode);
        return classRepository.getStudentsCount(lectureCode, sectionCode);
    }

    // 7. 특정 요일에 진행되는 강의 목록 보기 (12번 쿼리)
    @GetMapping("/day")
    public List<ClassInfo> getClassListByDayOfWeek(@SessionAttribute(name = "userId", required = false) String userId, @RequestParam int dayOfWeek) throws SQLException {
        isLogin(userId);
        return classRepository.getClassByDayOfWeek(dayOfWeek);
    }

    // 8. 수강 중인 수업의 게시글에 댓글 작성
    @PostMapping("/{lectureCode}/{sectionCode}/post/{postId}/comment")
    public void writeComment(@SessionAttribute(name = "userId", required = false) String userId,
                             @PathVariable String lectureCode, @PathVariable String sectionCode,
                             @RequestBody CreateCommentRequestDto commentRequestDto) throws SQLException {
        isLogin(userId);
        isTakingClass(userId, lectureCode, sectionCode);
        classRepository.writeComment(commentRequestDto.getPostId(), commentRequestDto.getContent(), userId);
    }

    private void isTakingClass(String studentId, String lectureCode, String sectionCode) throws SQLException {
        boolean isTakingClass = classRepository.isTakingClass(studentId, lectureCode, sectionCode);
        if (!isTakingClass) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수강하지 않는 과목입니다.");
        }
    }

    private void isLogin(String studentId) {
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }
}
