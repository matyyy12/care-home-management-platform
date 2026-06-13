package com.bd2.seniorhomeapp.report.service;

import java.util.List;

import com.bd2.seniorhomeapp.report.dto.RoomUtilizationReportRow;
import com.bd2.seniorhomeapp.room.entity.RoomType;

public interface RoomUtilizationReportService {

    List<RoomUtilizationReportRow> generateReport(Long seniorHomeId, Boolean inUse, RoomType type);
}
