package com.bd2.seniorhomeapp.senior.controller;

import static com.bd2.seniorhomeapp.security.SecurityTestSupport.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bd2.seniorhomeapp.senior.service.SeniorService;

@SpringBootTest
@AutoConfigureMockMvc
class SeniorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    
    @MockitoBean
    private SeniorService seniorService;

    @Test
    void employeeCanReadSeniors() throws Exception {
        mockMvc.perform(get("/api/seniors").with(asEmployee(1L)))
                .andExpect(status().isOk());
    }

    @Test
    void employeeCannotCreateSenior() throws Exception {
        mockMvc.perform(post("/api/seniors")
                        .with(asEmployee(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jan\",\"lastName\":\"Kowalski\",\"birthDate\":\"1950-01-15\",\"pesel\":\"50011512345\",\"diseases\":[],\"gender\":\"M\",\"accommodationDate\":\"2024-06-01\",\"seniorHomeId\":1}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Brak uprawnień do wykonania tej operacji"));
    }

    @Test
    void medicalCannotDeleteSenior() throws Exception {
        mockMvc.perform(delete("/api/seniors/1").with(asMedical(1L)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Brak uprawnień do wykonania tej operacji"));
    }

    @Test
    void adminTechCannotReadSeniors() throws Exception {
        mockMvc.perform(get("/api/seniors").with(asAdminTech()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Brak uprawnień do wykonania tej operacji"));
    }

    @Test
    void managerCanCreateSenior() throws Exception {
        mockMvc.perform(post("/api/seniors")
                        .with(asManager(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jan\",\"lastName\":\"Kowalski\",\"birthDate\":\"1950-01-15\",\"pesel\":\"50011512345\",\"diseases\":[],\"gender\":\"M\",\"accommodationDate\":\"2024-06-01\",\"seniorHomeId\":1}"))
                .andExpect(status().isCreated());
    }
}