package knu.database.lms.dto.controller;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ClassRoomAvailableRequest {
    private int buildingNumber;
    private String roomCode;
    private LocalDate date;
}
