package com.bd2.seniorhomeapp.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.report.dto.RoomOccupancyReportRow;
import com.bd2.seniorhomeapp.report.service.RoomOccupancyReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports/room-occupancy")
@RequiredArgsConstructor
public class RoomOccupancyReportController {

    private final RoomOccupancyReportService roomOccupancyReportService;

    @GetMapping
    public ResponseEntity<List<RoomOccupancyReportRow>> getReport(
            @RequestParam(required = false) Long seniorHomeId,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String standard) {
        return ResponseEntity.ok(roomOccupancyReportService.generateReport(seniorHomeId, floor, standard));
    }
}
