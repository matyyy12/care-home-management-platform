package com.bd2.seniorhomeapp.report.service;

import java.util.List;

import com.bd2.seniorhomeapp.report.dto.SeniorActivityReportRow;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;

public interface SeniorActivityReportService {
    List<SeniorActivityReportRow> generateReport(Long seniorHomeId, SeniorStatus status);
}
