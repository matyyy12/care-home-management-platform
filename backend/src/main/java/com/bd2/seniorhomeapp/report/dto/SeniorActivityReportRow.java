package com.bd2.seniorhomeapp.report.dto;

import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;

public record SeniorActivityReportRow(
        Long seniorId,
        String seniorName,
        Long seniorHomeId,
        String seniorHome,
        SeniorStatus status,
        String roomNumber,
        Integer participationCount,
        long availableActivityCount,
        long medicalActivityCount,
        long recreationalActivityCount) {
}
