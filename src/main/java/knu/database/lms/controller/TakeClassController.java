package knu.database.lms.controller;

import knu.database.lms.dto.ClassInfo;
import knu.database.lms.dto.Department;
import knu.database.lms.dto.TakeClassResult;
import knu.database.lms.dto.controller.TakeClassRequestDto;
import knu.database.lms.repositories.DepartmentRepository;
import knu.database.lms.repositories.TakeClassRepository;
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
public class TakeClassController {
	private final TakeClassRepository takeClassRepository;
	private final DepartmentRepository departmentRepository;

	@GetMapping("/sugang")
	public ModelAndView sugangPage(
			@RequestParam(name = "departmentCode", required = false) String departmentCode,
			@SessionAttribute(name = "userId", required = false) String userId
	) throws SQLException {
		ModelAndView mav = new ModelAndView();

		if (userId == null) {
			mav.setViewName("redirect:/");

			return mav;
		}
		mav.setViewName("sugang");

		List<ClassInfo> classInfos;
		if (departmentCode == null || departmentCode.equals("")) {
			classInfos = takeClassRepository.getClasses(2023, "2");
		}
		else {
			departmentCode = departmentCode.toUpperCase();
			Department department = departmentRepository.getDepartment(departmentCode);
			if (department == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 학과가 존재하지 않습니다.");
			}

			classInfos = takeClassRepository.getClassesOf(departmentCode, 2023, "2");
		}

		mav.addObject("classInfos", classInfos);

		return mav;
	}

	@PostMapping("/api/sugang")
	@ResponseBody
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

	@DeleteMapping("/api/sugang")
	@ResponseBody
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
