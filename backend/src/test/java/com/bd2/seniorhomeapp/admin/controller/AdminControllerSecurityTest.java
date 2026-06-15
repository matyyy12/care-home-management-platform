package com.bd2.seniorhomeapp.admin.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.bd2.seniorhomeapp.admin.dto.response.AdminBackupResponse;
import com.bd2.seniorhomeapp.admin.service.AdminService;
import com.bd2.seniorhomeapp.config.GlobalExceptionHandler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({AdminControllerSecurityTest.MethodSecurityTestConfig.class, GlobalExceptionHandler.class})
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminService adminService;

    @Test
    @WithMockUser(roles = "DIRECTOR")
    void createBackupShouldBeForbiddenForDirector() throws Exception {
        mockMvc.perform(post("/api/admin/backup"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Brak uprawnień do wykonania tej operacji"));
    }

    @Test
    @WithMockUser(roles = "ADMIN_TECH")
    void createBackupShouldBeAllowedForAdminTech() throws Exception {
        when(adminService.createBackup())
                .thenReturn(new AdminBackupResponse("ok", "2026-06-13T10:00:00Z"));

        mockMvc.perform(post("/api/admin/backup"))
                .andExpect(status().isOk());

        verify(adminService).createBackup();
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {

        @Bean
        AdminService adminService() {
            return mock(AdminService.class);
        }
    }
}
