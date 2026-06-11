package com.bd2.seniorhomeapp.report.service;

public interface SeniorHomeSummaryPdfService {
    byte[] generatePdf(Long seniorHomeId);
}
