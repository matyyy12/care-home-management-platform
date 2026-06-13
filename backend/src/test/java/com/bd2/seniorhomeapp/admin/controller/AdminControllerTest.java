package com.bd2.seniorhomeapp.admin.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bd2.seniorhomeapp.admin.dto.response.AdminBackupResponse;
import com.bd2.seniorhomeapp.admin.dto.response.AdminConfigResponse;
import com.bd2.seniorhomeapp.admin.dto.response.AdminHealthResponse;
import com.bd2.seniorhomeapp.admin.service.AdminService;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminController(adminService))
                .build();
    }

    @Test
    void getHealthShouldReturnHealthResponse() throws Exception {
        when(adminService.getHealth())
                .thenReturn(new AdminHealthResponse("UP", true, "backend"));

        mockMvc.perform(get("/api/admin/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.databaseConnected").value(true))
                .andExpect(jsonPath("$.applicationName").value("backend"));

        verify(adminService).getHealth();
    }

    @Test
    void getConfigShouldReturnConfigResponse() throws Exception {
        when(adminService.getConfig())
                .thenReturn(new AdminConfigResponse(86400L, true, "http://localhost:5173"));

        mockMvc.perform(get("/api/admin/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtExpirationSeconds").value(86400))
                .andExpect(jsonPath("$.sampleDataEnabled").value(true))
                .andExpect(jsonPath("$.corsAllowedOrigins").value("http://localhost:5173"));

        verify(adminService).getConfig();
    }

    @Test
    void createBackupShouldReturnBackupResponse() throws Exception {
        when(adminService.createBackup())
                .thenReturn(new AdminBackupResponse("Kopia zapasowa została zaplanowana (stub).", "2026-06-13T10:00:00Z"));

        mockMvc.perform(post("/api/admin/backup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Kopia zapasowa została zaplanowana (stub)."))
                .andExpect(jsonPath("$.timestamp").value("2026-06-13T10:00:00Z"));

        verify(adminService).createBackup();
    }
}
