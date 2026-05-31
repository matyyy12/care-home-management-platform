package com.bd2.seniorhomeapp.position;

import com.bd2.seniorhomeapp.position.controller.PositionController;
import com.bd2.seniorhomeapp.position.dto.request.PositionRequest;
import com.bd2.seniorhomeapp.position.entity.Position;
import com.bd2.seniorhomeapp.position.service.PositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PositionControllerTest {

    @Mock
    private PositionService positionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PositionController(positionService)).build();
    }

    @Test
    void getPositionsShouldReturnPositionResponses() throws Exception {
        Position position = position(1L, "Opiekun", "Opiekuje sie seniorami");
        when(positionService.getPositions()).thenReturn(List.of(position));

        mockMvc.perform(get("/api/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Opiekun"))
                .andExpect(jsonPath("$[0].description").value("Opiekuje sie seniorami"));

        verify(positionService).getPositions();
    }

    @Test
    void createPositionShouldReturnCreatedPositionResponse() throws Exception {
        Position position = position(1L, "Opiekun", "Opiekuje sie seniorami");
        when(positionService.createPosition(any(PositionRequest.class))).thenReturn(position);

        mockMvc.perform(post("/api/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Opiekun",
                                  "description": "Opiekuje sie seniorami"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Opiekun"));

        verify(positionService).createPosition(any(PositionRequest.class));
    }

    @Test
    void updatePositionShouldReturnUpdatedPositionResponse() throws Exception {
        Position position = position(1L, "Kierownik", "Zarzadza pracownikami");
        when(positionService.updatePosition(any(), eq(1L))).thenReturn(position);

        mockMvc.perform(patch("/api/positions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Kierownik"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Kierownik"));

        verify(positionService).updatePosition(any(), eq(1L));
    }

    @Test
    void deletePositionShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/positions/1"))
                .andExpect(status().isNoContent());

        verify(positionService).deletePosition(1L);
    }

    private static Position position(Long id, String name, String description) {
        Position position = new Position();
        position.setId(id);
        position.setName(name);
        position.setDescription(description);
        return position;
    }
}
