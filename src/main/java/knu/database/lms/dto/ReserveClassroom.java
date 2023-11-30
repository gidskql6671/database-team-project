package knu.database.lms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReserveClassroom {
    private int reservedId;
    private int buildingNumber;
    private String roomCode;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public ReserveClassroom(int reservedId, int buildingNumber, String roomCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.reservedId = reservedId;
        this.buildingNumber = buildingNumber;
        this.roomCode = roomCode;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}
