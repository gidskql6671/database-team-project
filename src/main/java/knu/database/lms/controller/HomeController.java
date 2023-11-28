package knu.database.lms.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequiredArgsConstructor
public class HomeController {

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

	@GetMapping("/classroom")
	public ModelAndView classroomPage(@SessionAttribute(name = "userId", required = false) String userId) {
		ModelAndView mav = new ModelAndView();

		if (userId == null) {
			mav.setViewName("redirect:/");
		}
		else {
			mav.setViewName("classroom");
		}

		return mav;
	}
}