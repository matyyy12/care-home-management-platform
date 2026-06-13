package com.bd2.seniorhomeapp.report.dto;

import com.bd2.seniorhomeapp.room.entity.RoomType;

public record RoomUtilizationReportRow(
        Long roomId,
        String roomNumber,
        Integer floor,
        String seniorHome,
        Long seniorHomeId,
        Boolean inUse,
        RoomType type,
        int seniorCount,
        int employeeCount,
        int activityCount,
        boolean inUseWithoutType,
        boolean hasWarning,
        String warningMessage) {
}
