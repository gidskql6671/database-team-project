package dto;

public class Classroom {
    public int buildingNumber;
    public String roomCode;
    public String name;

    public Classroom(int buildingNumber, String roomCode, String name) {
        this.buildingNumber = buildingNumber;
        this.roomCode = roomCode;
        this.name = name;
    }

    public Classroom(int buildingNumber) {
        this(
                buildingNumber,
                "",
                ""
        );
    }

    public Classroom(String name) {
        this(
                0,
                "",
                name
        );
    }
}

