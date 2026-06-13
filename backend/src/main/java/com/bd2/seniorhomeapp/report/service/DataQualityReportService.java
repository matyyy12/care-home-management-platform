package com.bd2.seniorhomeapp.report.service;

import java.util.List;

import com.bd2.seniorhomeapp.report.dto.DataQualityIssueRow;

public interface DataQualityReportService {
    List<DataQualityIssueRow> generateReport(Long seniorHomeId, String severity);
}
