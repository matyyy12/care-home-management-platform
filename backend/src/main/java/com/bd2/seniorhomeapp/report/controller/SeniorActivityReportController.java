package com.bd2.seniorhomeapp.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.report.dto.SeniorActivityReportRow;
import com.bd2.seniorhomeapp.report.service.SeniorActivityReportService;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports/senior-activity")
@RequiredArgsConstructor
public class SeniorActivityReportController {

    private final SeniorActivityReportService seniorActivityReportService;

    @GetMapping
    public ResponseEntity<List<SeniorActivityReportRow>> getReport(
            @RequestParam(required = false) Long seniorHomeId,
            @RequestParam(required = false) SeniorStatus status) {
        return ResponseEntity.ok(seniorActivityReportService.generateReport(seniorHomeId, status));
    }
}
