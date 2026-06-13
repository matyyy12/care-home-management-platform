package com.bd2.seniorhomeapp.report.service;

import java.util.List;

import com.bd2.seniorhomeapp.report.dto.EmployeeWorkloadReportRow;

public interface EmployeeWorkloadReportService {
    List<EmployeeWorkloadReportRow> generateReport(
            Long seniorHomeId,
            Long positionId,
            Boolean medicalOnly,
            String employmentStatus);
}
