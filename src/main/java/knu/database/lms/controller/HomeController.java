package knu.database.lms.controller;


import knu.database.lms.repositories.DepartmentRepository;
import knu.database.lms.repositories.TakeClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;


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
}