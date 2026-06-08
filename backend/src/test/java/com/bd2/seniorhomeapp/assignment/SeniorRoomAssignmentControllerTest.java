package com.bd2.seniorhomeapp.assignment;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bd2.seniorhomeapp.assignment.controller.SeniorRoomAssignmentController;
import com.bd2.seniorhomeapp.assignment.dto.request.AssignSeniorToRoomRequest;
import com.bd2.seniorhomeapp.assignment.dto.response.SeniorRoomAssignmentResponse;
import com.bd2.seniorhomeapp.assignment.service.SeniorRoomAssignmentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SeniorRoomAssignmentControllerTest {

    @Mock
    private SeniorRoomAssignmentService seniorRoomAssignmentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SeniorRoomAssignmentController(seniorRoomAssignmentService))
                .build();
    }

    @Test
    void getAssignmentsShouldReturnAssignmentList() throws Exception {
        when(seniorRoomAssignmentService.findAssignments(null))
                .thenReturn(List.of(sampleAssignment()));

        mockMvc.perform(get("/api/assignments/senior-rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seniorName").value("Helena Kowalczyk"))
                .andExpect(jsonPath("$[0].roomNumber").value("101"));

        verify(seniorRoomAssignmentService).findAssignments(null);
    }

    @Test
    void assignShouldReturnCreatedAssignment() throws Exception {
        when(seniorRoomAssignmentService.assign(any(AssignSeniorToRoomRequest.class)))
                .thenReturn(sampleAssignment());

        mockMvc.perform(post("/api/assignments/senior-rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "seniorId": 1,
                                  "roomId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNumber").value("101"));

        verify(seniorRoomAssignmentService).assign(any(AssignSeniorToRoomRequest.class));
    }

    @Test
    void unassignShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/assignments/senior-rooms/1"))
                .andExpect(status().isNoContent());

        verify(seniorRoomAssignmentService).unassign(eq(1L));
    }

    private static SeniorRoomAssignmentResponse sampleAssignment() {
        return new SeniorRoomAssignmentResponse(
                1L,
                "Helena Kowalczyk",
                1L,
                "101",
                1L,
                "Złota Jesień");
    }
}
