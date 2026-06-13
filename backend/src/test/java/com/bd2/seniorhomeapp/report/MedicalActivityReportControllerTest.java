package com.bd2.seniorhomeapp.report;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bd2.seniorhomeapp.report.controller.MedicalActivityReportController;
import com.bd2.seniorhomeapp.report.dto.MedicalActivityReportRow;
import com.bd2.seniorhomeapp.report.service.MedicalActivityReportService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MedicalActivityReportControllerTest {

    @Mock
    private MedicalActivityReportService medicalActivityReportService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MedicalActivityReportController(medicalActivityReportService))
                .build();
    }

    @Test
    void getReportShouldReturnMedicalActivityIssues() throws Exception {
        when(medicalActivityReportService.generateReport(isNull()))
                .thenReturn(List.of(sampleRow()));

        mockMvc.perform(get("/api/reports/medical-activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activityName").value("Fizjoterapia"))
                .andExpect(jsonPath("$[0].issueType").value("NO_MEDICAL_LEAD"));

        verify(medicalActivityReportService).generateReport(isNull());
    }

    @Test
    void getReportShouldPassSeniorHomeFilter() throws Exception {
        when(medicalActivityReportService.generateReport(eq(2L)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/reports/medical-activities").param("seniorHomeId", "2"))
                .andExpect(status().isOk());

        verify(medicalActivityReportService).generateReport(2L);
    }

    private static MedicalActivityReportRow sampleRow() {
        return new MedicalActivityReportRow(
                1L,
                "Fizjoterapia",
                "Złota Jesień",
                1L,
                "NO_MEDICAL_LEAD",
                "Brak pracownika medycznego prowadzącego zajęcia");
    }
}
