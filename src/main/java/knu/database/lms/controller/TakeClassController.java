package knu.database.lms.controller;

import knu.database.lms.dto.ClassInfo;
import knu.database.lms.dto.TakeClassResult;
import knu.database.lms.dto.controller.TakeClassRequestDto;
import knu.database.lms.repositories.TakeClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/sugang")
@RequiredArgsConstructor
public class TakeClassController {
	private final TakeClassRepository takeClassRepository;

	@GetMapping("/class")
	public List<ClassInfo> getClasses() throws SQLException {
		return takeClassRepository.getClasses(2023, "2");
	}

	@PostMapping
	public void takeClass(
			@RequestBody TakeClassRequestDto req,
			@SessionAttribute(name = "userId", required = false) String userId
			) throws SQLException {
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		TakeClassResult result = takeClassRepository.takeClass(userId, req.getLectureCode(), req.getSectionCode());

		if (!result.isSuccess) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.message);
		}
	}

	@DeleteMapping
	public void unTakeClass(
			@RequestBody TakeClassRequestDto req,
			@SessionAttribute(name = "userId", required = false) String userId
	) throws SQLException {
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		boolean result = takeClassRepository.untakeClass(userId, req.getLectureCode(), req.getSectionCode());

		if (!result) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수강 신청 취소가 실패했습니다.");
		}
	}
}
