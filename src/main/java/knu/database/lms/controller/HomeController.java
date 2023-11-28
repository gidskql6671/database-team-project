package knu.database.lms.controller;


import knu.database.lms.dto.ClassInfo;
import knu.database.lms.dto.Department;
import knu.database.lms.repositories.DepartmentRepository;
import knu.database.lms.repositories.TakeClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class HomeController {
	private final TakeClassRepository takeClassRepository;
	private final DepartmentRepository departmentRepository;

	@GetMapping
	public ModelAndView mainPage(@SessionAttribute(name = "userId", required = false) String userId) {
		ModelAndView mav = new ModelAndView();

		if (userId == null) {
			mav.addObject("isLogin", false);
		}
		else {
			mav.addObject("isLogin", true);
		}
		mav.setViewName("index");

		return mav;
	}

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

	@ExceptionHandler(ResponseStatusException.class)
	@GetMapping("/error")
	public String handleError(ResponseStatusException e, Model model) {
		model.addAttribute("code", e.getStatusCode());
		model.addAttribute("msg", e.getMessage());

		return "error";
	}
}