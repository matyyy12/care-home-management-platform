package com.bd2.seniorhomeapp.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.report.dto.EmployeeWorkloadReportRow;
import com.bd2.seniorhomeapp.report.service.EmployeeWorkloadReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports/employee-workload")
@RequiredArgsConstructor
public class EmployeeWorkloadReportController {

    private final EmployeeWorkloadReportService employeeWorkloadReportService;

    @GetMapping
    public ResponseEntity<List<EmployeeWorkloadReportRow>> getReport(
            @RequestParam(required = false) Long seniorHomeId,
            @RequestParam(required = false) Long positionId,
            @RequestParam(required = false) Boolean medicalOnly,
            @RequestParam(required = false) String employmentStatus) {
        return ResponseEntity.ok(employeeWorkloadReportService.generateReport(
                seniorHomeId,
                positionId,
                medicalOnly,
                employmentStatus));
    }
}
