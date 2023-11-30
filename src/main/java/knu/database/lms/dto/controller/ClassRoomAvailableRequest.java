package knu.database.lms.dto.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ClassRoomAvailableRequest {
    private int buildingNumber;
    private String roomCode;
    private LocalDate date;
}
