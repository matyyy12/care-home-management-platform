package com.bd2.seniorhomeapp.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.report.dto.MedicalActivityReportRow;
import com.bd2.seniorhomeapp.report.service.MedicalActivityReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports/medical-activities")
@RequiredArgsConstructor
public class MedicalActivityReportController {

    private final MedicalActivityReportService medicalActivityReportService;

    @GetMapping
    public ResponseEntity<List<MedicalActivityReportRow>> getReport(
            @RequestParam(required = false) Long seniorHomeId) {
        return ResponseEntity.ok(medicalActivityReportService.generateReport(seniorHomeId));
    }
}
