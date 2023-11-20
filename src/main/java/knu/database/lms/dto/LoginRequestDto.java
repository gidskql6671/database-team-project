package knu.database.lms.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginRequestDto {
	private String loginId;
	private String loginPassword;
}
