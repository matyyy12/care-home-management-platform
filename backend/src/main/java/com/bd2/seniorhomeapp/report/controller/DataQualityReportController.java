package com.bd2.seniorhomeapp.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.report.dto.DataQualityIssueRow;
import com.bd2.seniorhomeapp.report.service.DataQualityReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports/data-quality")
@RequiredArgsConstructor
public class DataQualityReportController {

    private final DataQualityReportService dataQualityReportService;

    @GetMapping
    public ResponseEntity<List<DataQualityIssueRow>> getReport(
            @RequestParam(required = false) Long seniorHomeId,
            @RequestParam(required = false) String severity) {
        return ResponseEntity.ok(dataQualityReportService.generateReport(seniorHomeId, severity));
    }
}
