package com.bd2.seniorhomeapp.report.dto;

public record EmployeeWorkloadReportRow(
        Long employeeId,
        String employeeName,
        Long seniorHomeId,
        String seniorHome,
        Long positionId,
        String positionName,
        String managerName,
        boolean medicalEmployee,
        long seniorCount,
        long roomCount,
        long activityCount,
        long medicalActivityCount,
        long totalAssignments,
        long workloadScore,
        String employmentStatus,
        boolean hasWarning,
        String warningMessage) {
}
