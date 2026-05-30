package com.bd2.seniorhomeapp.postOffice;

import com.bd2.seniorhomeapp.postOffice.controller.PostOfficeController;
import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeRequest;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.postOffice.service.PostOfficeService;
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
class PostOfficeControllerTest {

    @Mock
    private PostOfficeService postOfficeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PostOfficeController(postOfficeService)).build();
    }

    @Test
    void getAllPostOfficesShouldReturnPostOfficeResponses() throws Exception {
        PostOffice postOffice = postOffice(1L, "00-001", "Warszawa");
        when(postOfficeService.getPostOffices(null, "Warszawa")).thenReturn(List.of(postOffice));

        mockMvc.perform(get("/api/post-offices").param("city", "Warszawa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].postalCode").value("00-001"))
                .andExpect(jsonPath("$[0].city").value("Warszawa"));

        verify(postOfficeService).getPostOffices(null, "Warszawa");
    }

    @Test
    void createPostOfficeShouldReturnCreatedPostOfficeResponse() throws Exception {
        PostOffice postOffice = postOffice(1L, "00-001", "Warszawa");
        when(postOfficeService.createPostOffice(any(PostOfficeRequest.class))).thenReturn(postOffice);

        mockMvc.perform(post("/api/post-offices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "postalCode": "00-001",
                                  "city": "Warszawa"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.postalCode").value("00-001"));

        verify(postOfficeService).createPostOffice(any(PostOfficeRequest.class));
    }

    @Test
    void updatePostOfficeShouldReturnUpdatedPostOfficeResponse() throws Exception {
        PostOffice postOffice = postOffice(1L, "30-001", "Krakow");
        when(postOfficeService.updatePostOffice(any(), eq(1L))).thenReturn(postOffice);

        mockMvc.perform(patch("/api/post-offices")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "city": "Krakow"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.city").value("Krakow"));

        verify(postOfficeService).updatePostOffice(any(), eq(1L));
    }

    @Test
    void deletePostOfficeShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/post-offices").param("id", "1"))
                .andExpect(status().isNoContent());

        verify(postOfficeService).deletePostOffice(1L);
    }

    private static PostOffice postOffice(Long id, String postalCode, String city) {
        PostOffice postOffice = new PostOffice();
        postOffice.setId(id);
        postOffice.setPostalCode(postalCode);
        postOffice.setCity(city);
        return postOffice;
    }
}
