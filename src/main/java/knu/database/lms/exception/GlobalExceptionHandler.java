package knu.database.lms.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SQLException.class)
	public ResponseEntity<String> handleSqlException(SQLException e) {
		System.out.println(e.getMessage());
		return ResponseEntity
				.internalServerError()
				.body("SQL 예외가 발생했습니다.");
	}

}
