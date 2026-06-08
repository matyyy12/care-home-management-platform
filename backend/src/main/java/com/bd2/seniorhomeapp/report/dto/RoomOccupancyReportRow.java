package com.bd2.seniorhomeapp.report.dto;

public record RoomOccupancyReportRow(
        Long roomId,
        String roomNumber,
        Integer floor,
        String seniorHome,
        Long seniorHomeId,
        Integer capacity,
        int assigned,
        int free,
        String standard
) {
}
