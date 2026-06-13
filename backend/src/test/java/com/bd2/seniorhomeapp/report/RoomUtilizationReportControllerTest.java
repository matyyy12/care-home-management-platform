package com.bd2.seniorhomeapp.report;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bd2.seniorhomeapp.report.controller.RoomUtilizationReportController;
import com.bd2.seniorhomeapp.report.dto.RoomUtilizationReportRow;
import com.bd2.seniorhomeapp.report.service.RoomUtilizationReportService;
import com.bd2.seniorhomeapp.room.entity.RoomType;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoomUtilizationReportControllerTest {

    @Mock
    private RoomUtilizationReportService roomUtilizationReportService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RoomUtilizationReportController(roomUtilizationReportService))
                .build();
    }

    @Test
    void getReportShouldReturnUtilizationRows() throws Exception {
        when(roomUtilizationReportService.generateReport(isNull(), isNull(), isNull()))
                .thenReturn(List.of(sampleRow()));

        mockMvc.perform(get("/api/reports/room-utilization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNumber").value("101"))
                .andExpect(jsonPath("$[0].seniorCount").value(2))
                .andExpect(jsonPath("$[0].employeeCount").value(1))
                .andExpect(jsonPath("$[0].activityCount").value(3));

        verify(roomUtilizationReportService).generateReport(isNull(), isNull(), isNull());
    }

    @Test
    void getReportShouldPassFilters() throws Exception {
        when(roomUtilizationReportService.generateReport(eq(1L), eq(true), eq(RoomType.OFFICE)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/reports/room-utilization")
                        .param("seniorHomeId", "1")
                        .param("inUse", "true")
                        .param("type", "OFFICE"))
                .andExpect(status().isOk());

        verify(roomUtilizationReportService).generateReport(1L, true, RoomType.OFFICE);
    }

    private static RoomUtilizationReportRow sampleRow() {
        return new RoomUtilizationReportRow(
                1L,
                "101",
                1,
                "Złota Jesień",
                1L,
                true,
                RoomType.RESIDENTIAL,
                2,
                1,
                3,
                false,
                false,
                null);
    }
}
