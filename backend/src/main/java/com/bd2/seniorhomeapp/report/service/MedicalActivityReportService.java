package com.bd2.seniorhomeapp.report.service;

import java.util.List;

import com.bd2.seniorhomeapp.report.dto.MedicalActivityReportRow;

public interface MedicalActivityReportService {

    List<MedicalActivityReportRow> generateReport(Long seniorHomeId);
}
