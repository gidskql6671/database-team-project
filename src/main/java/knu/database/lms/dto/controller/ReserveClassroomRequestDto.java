package knu.database.lms.dto.controller;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReserveClassroomRequestDto {
    private int buildingNumber;
    private String roomCode;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
