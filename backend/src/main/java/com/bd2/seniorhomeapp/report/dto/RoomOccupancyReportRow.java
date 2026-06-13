package com.bd2.seniorhomeapp.report.dto;

import com.bd2.seniorhomeapp.room.entity.RoomStandard;

public record RoomOccupancyReportRow(
        Long roomId,
        String roomNumber,
        Integer floor,
        String seniorHome,
        Long seniorHomeId,
        Integer capacity,
        int assigned,
        int free,
        RoomStandard standard
) {
}
