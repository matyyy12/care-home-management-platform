package com.bd2.seniorhomeapp.authentication;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bd2.seniorhomeapp.authentication.controller.AuthenticationController;
import com.bd2.seniorhomeapp.authentication.dto.request.LoginRequest;
import com.bd2.seniorhomeapp.authentication.dto.response.AuthUserResponse;
import com.bd2.seniorhomeapp.authentication.dto.response.LoginResponse;
import com.bd2.seniorhomeapp.authentication.entity.AppRole;
import com.bd2.seniorhomeapp.authentication.service.AuthenticationService;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock
    private AuthenticationService authenticationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthenticationController(authenticationService)).build();
    }

    @Test
    void loginShouldReturnTokenAndUser() throws Exception {
        when(authenticationService.login(new LoginRequest("manager@seniorhouse.local", "Password123!")))
                .thenReturn(new LoginResponse(
                        "jwt-token",
                        new AuthUserResponse(1L, "manager@seniorhouse.local", AppRole.MANAGER, 1L)
                ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "manager@seniorhouse.local",
                                  "password": "Password123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("manager@seniorhouse.local"))
                .andExpect(jsonPath("$.user.role").value("MANAGER"))
                .andExpect(jsonPath("$.user.assignedSeniorHomeId").value(1));
    }

    @Test
    void meShouldReturnCurrentUserProfile() throws Exception {
        when(authenticationService.currentUser("manager@seniorhouse.local"))
                .thenReturn(new AuthUserResponse(1L, "manager@seniorhouse.local", AppRole.MANAGER, 1L));

        mockMvc.perform(get("/api/auth/me").principal(() -> "manager@seniorhouse.local"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("manager@seniorhouse.local"))
                .andExpect(jsonPath("$.role").value("MANAGER"));

        verify(authenticationService).currentUser("manager@seniorhouse.local");
    }
}
