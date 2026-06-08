package com.bd2.seniorhomeapp.report;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bd2.seniorhomeapp.report.controller.RoomOccupancyReportController;
import com.bd2.seniorhomeapp.report.dto.RoomOccupancyReportRow;
import com.bd2.seniorhomeapp.report.service.RoomOccupancyReportService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoomOccupancyReportControllerTest {

    @Mock
    private RoomOccupancyReportService roomOccupancyReportService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RoomOccupancyReportController(roomOccupancyReportService))
                .build();
    }

    @Test
    void getReportShouldReturnOccupancyRows() throws Exception {
        when(roomOccupancyReportService.generateReport(isNull(), isNull(), isNull()))
                .thenReturn(List.of(sampleRow()));

        mockMvc.perform(get("/api/reports/room-occupancy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNumber").value("101"))
                .andExpect(jsonPath("$[0].assigned").value(1))
                .andExpect(jsonPath("$[0].free").value(1));

        verify(roomOccupancyReportService).generateReport(isNull(), isNull(), isNull());
    }

    @Test
    void getReportShouldPassFilters() throws Exception {
        when(roomOccupancyReportService.generateReport(eq(1L), eq(1), eq("Podstawowy")))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/reports/room-occupancy")
                        .param("seniorHomeId", "1")
                        .param("floor", "1")
                        .param("standard", "Podstawowy"))
                .andExpect(status().isOk());

        verify(roomOccupancyReportService).generateReport(1L, 1, "Podstawowy");
    }

    private static RoomOccupancyReportRow sampleRow() {
        return new RoomOccupancyReportRow(
                1L,
                "101",
                1,
                "Złota Jesień",
                1L,
                2,
                1,
                1,
                "Podstawowy");
    }
}
