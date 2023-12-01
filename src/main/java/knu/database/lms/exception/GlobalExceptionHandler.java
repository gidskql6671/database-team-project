package knu.database.lms.exception;

import jakarta.servlet.http.HttpServletRequest;
import knu.database.lms.dto.controller.ExceptionResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SQLException.class)
	@ResponseBody
	public ResponseEntity<ExceptionResponse> handleSqlException(SQLException e) {
		System.out.println(e.getMessage());
		return ResponseEntity
				.internalServerError()
				.body(new ExceptionResponse(e.getMessage()));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ModelAndView handleError(HttpServletRequest req, ResponseStatusException e) {

		ModelAndView mav = new ModelAndView();
		String contentType = req.getContentType();
		if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)){
			mav.setStatus(e.getStatusCode());
			mav.setViewName("jsonView");

			System.out.println(e.getMessage());
			mav.addObject("msg", e.getReason());
		} else {
			//json 이 아닐경우 error page 로 이동
			mav.addObject("code", e.getStatusCode());
			mav.addObject("msg", e.getReason());

			mav.setViewName("error");
		}

		return mav;
	}
}
