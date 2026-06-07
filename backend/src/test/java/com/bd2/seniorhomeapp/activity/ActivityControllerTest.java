package com.bd2.seniorhomeapp.activity;

import com.bd2.seniorhomeapp.activity.controller.ActivityController;
import com.bd2.seniorhomeapp.activity.dto.request.ActivityRequest;
import com.bd2.seniorhomeapp.activity.entity.Activity;
import com.bd2.seniorhomeapp.activity.entity.ActivityType;
import com.bd2.seniorhomeapp.activity.service.ActivityService;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
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
class ActivityControllerTest {

    @Mock
    private ActivityService activityService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ActivityController(activityService)).build();
    }

    @Test
    void getActivitiesFromSeniorHomeShouldReturnActivityResponses() throws Exception {
        Activity activity = sampleActivity(1L);
        when(activityService.getActivitiesFromSeniorHome(1L)).thenReturn(List.of(activity));

        mockMvc.perform(get("/api/activities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Gimnastyka lekka"))
                .andExpect(jsonPath("$[0].type").value("RECREATIONAL"))
                .andExpect(jsonPath("$[0].seniorHome").value("Złota Jesień"));

        verify(activityService).getActivitiesFromSeniorHome(1L);
    }

    @Test
    void createActivityShouldReturnCreatedActivityResponse() throws Exception {
        Activity activity = sampleActivity(1L);
        when(activityService.createActivity(any(ActivityRequest.class))).thenReturn(activity);

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Gimnastyka lekka",
                                  "type": "RECREATIONAL",
                                  "price": 0,
                                  "seniorHomeId": 1,
                                  "description": "Ćwiczenia poranne"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gimnastyka lekka"));

        verify(activityService).createActivity(any(ActivityRequest.class));
    }

    @Test
    void updateActivityShouldReturnUpdatedActivityResponse() throws Exception {
        Activity activity = sampleActivity(1L);
        activity.setName("Zaktualizowane zajęcia");
        when(activityService.updateActivity(any(), eq(1L))).thenReturn(activity);

        mockMvc.perform(patch("/api/activities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Zaktualizowane zajęcia"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zaktualizowane zajęcia"));

        verify(activityService).updateActivity(any(), eq(1L));
    }

    @Test
    void deleteActivityShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/activities/1"))
                .andExpect(status().isNoContent());

        verify(activityService).deleteActivity(1L);
    }

    private static Activity sampleActivity(Long id) {
        SeniorHome seniorHome = new SeniorHome();
        seniorHome.setId(1L);
        seniorHome.setName("Złota Jesień");

        Activity activity = new Activity();
        activity.setId(id);
        activity.setName("Gimnastyka lekka");
        activity.setType(ActivityType.RECREATIONAL);
        activity.setPrice(BigDecimal.ZERO);
        activity.setDescription("Ćwiczenia poranne");
        activity.setSeniorHome(seniorHome);
        return activity;
    }
}
