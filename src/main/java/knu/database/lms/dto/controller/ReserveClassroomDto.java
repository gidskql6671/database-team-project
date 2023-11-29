package knu.database.lms.dto.controller;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReserveClassroomDto {
    private int buildingNumber;
    private String roomCode;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
