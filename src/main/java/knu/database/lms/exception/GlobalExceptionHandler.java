package knu.database.lms.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SQLException.class)
	@ResponseBody
	public ResponseEntity<String> handleSqlException(SQLException e) {
		System.out.println(e.getMessage());
		return ResponseEntity
				.internalServerError()
				.body("SQL 예외가 발생했습니다.");
	}

	@ExceptionHandler(ResponseStatusException.class)
//	@GetMapping("/error")
	public String handleError(ResponseStatusException e, Model model) {
		model.addAttribute("code", e.getStatusCode());
		model.addAttribute("msg", e.getMessage());

		return "error";
	}
}
