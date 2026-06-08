package com.bd2.seniorhomeapp.report.service;

import java.util.List;

import com.bd2.seniorhomeapp.report.dto.RoomOccupancyReportRow;

public interface RoomOccupancyReportService {

    List<RoomOccupancyReportRow> generateReport(Long seniorHomeId, Integer floor, String standard);
}
