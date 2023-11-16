package knu.database.lms.controller;

import knu.database.lms.dto.Classroom;
import knu.database.lms.repositories.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {

	private final ClassroomRepository classroomRepository;

	@GetMapping
	public List<Classroom> test() {
		try {
			return classroomRepository.getBuildings();
		}
		catch (SQLException e) {
			return List.of();
		}
	}
}
