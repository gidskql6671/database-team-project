package knu.database.lms.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import knu.database.lms.dto.LoginRequestDto;
import knu.database.lms.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final AccountRepository accountRepository;

	@PostMapping("/login")
	public void login(@RequestBody LoginRequestDto req, HttpServletRequest httpServletRequest) throws SQLException {
		String studentId = accountRepository.login(req.getLoginId(), req.getLoginPassword());

		if (studentId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 실패");
		}
		else {
			httpServletRequest.getSession().invalidate();
			HttpSession session = httpServletRequest.getSession();

			session.setAttribute("userId", studentId);
			session.setMaxInactiveInterval(1800);
		}
	}

	@DeleteMapping("/logout")
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
	}
}
