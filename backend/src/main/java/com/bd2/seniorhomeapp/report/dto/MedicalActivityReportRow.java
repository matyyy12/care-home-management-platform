package com.bd2.seniorhomeapp.report.dto;

public record MedicalActivityReportRow(
        Long activityId,
        String activityName,
        String seniorHome,
        Long seniorHomeId,
        String issueType,
        String description) {
}
