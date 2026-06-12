package com.bd2.seniorhomeapp.report.dto;

public record DataQualityIssueRow(
        String severity,
        String module,
        String objectType,
        Long objectId,
        String objectName,
        Long seniorHomeId,
        String seniorHome,
        String description,
        String suggestedAction) {
}
