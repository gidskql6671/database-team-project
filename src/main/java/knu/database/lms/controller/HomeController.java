package knu.database.lms.controller;

import knu.database.lms.dto.Classroom;
import knu.database.lms.repositories.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final ClassroomRepository classroomRepository;

	@GetMapping
	public void test(@SessionAttribute(name = "userId", required = false) Long userId) throws SQLException {
		ModelAndView mav = new ModelAndView();

		throw new SQLException("에러");
//		if (userId == null) {
//			mav.addObject("isLogin", false);
//		}
//		else {
//			mav.addObject("isLogin", true);
//		}
//		mav.setViewName("index");
//
//		return mav;
	}
}
