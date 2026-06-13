package com.bd2.seniorhomeapp.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.report.dto.RoomUtilizationReportRow;
import com.bd2.seniorhomeapp.report.service.RoomUtilizationReportService;
import com.bd2.seniorhomeapp.room.entity.RoomType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports/room-utilization")
@RequiredArgsConstructor
public class RoomUtilizationReportController {

    private final RoomUtilizationReportService roomUtilizationReportService;

    @GetMapping
    public ResponseEntity<List<RoomUtilizationReportRow>> getReport(
            @RequestParam(required = false) Long seniorHomeId,
            @RequestParam(required = false) Boolean inUse,
            @RequestParam(required = false) RoomType type) {
        return ResponseEntity.ok(roomUtilizationReportService.generateReport(seniorHomeId, inUse, type));
    }
}
