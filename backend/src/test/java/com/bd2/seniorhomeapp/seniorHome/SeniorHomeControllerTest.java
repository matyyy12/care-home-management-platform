package com.bd2.seniorhomeapp.seniorHome;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.seniorHome.controller.SeniorHomeController;
import com.bd2.seniorhomeapp.seniorHome.dto.request.SeniorHomeRequest;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.service.SeniorHomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
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
class SeniorHomeControllerTest {

    @Mock
    private SeniorHomeService seniorHomeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SeniorHomeController(seniorHomeService)).build();
    }

    @Test
    void getHomesShouldReturnSeniorHomeResponses() throws Exception {
        SeniorHome seniorHome = seniorHome(1L, "Dom Seniora", "123456789", "kontakt@dom.pl", "dom.pl", address());
        when(seniorHomeService.findHomes(1L)).thenReturn(List.of(seniorHome));

        mockMvc.perform(get("/api/senior-home").param("homeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dom Seniora"))
                .andExpect(jsonPath("$[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$[0].email").value("kontakt@dom.pl"))
                .andExpect(jsonPath("$[0].website").value("dom.pl"))
                .andExpect(jsonPath("$[0].city").value("Warszawa"))
                .andExpect(jsonPath("$[0].street").value("Polna"))
                .andExpect(jsonPath("$[0].number").value("10"));

        verify(seniorHomeService).findHomes(1L);
    }

    @Test
    void createHomeShouldReturnCreatedSeniorHomeResponse() throws Exception {
        SeniorHome seniorHome = seniorHome(1L, "Dom Seniora", "123456789", null, null, address());
        when(seniorHomeService.createHome(any(SeniorHomeRequest.class))).thenReturn(seniorHome);

        mockMvc.perform(post("/api/senior-home")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Dom Seniora",
                                  "phoneNumber": "123456789",
                                  "establishedDate": "2020-01-01",
                                  "address": {
                                    "city": "Warszawa",
                                    "street": "Polna",
                                    "number": "10",
                                    "postOffice": {
                                      "postalCode": "00-001",
                                      "city": "Warszawa"
                                    }
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dom Seniora"));

        verify(seniorHomeService).createHome(any(SeniorHomeRequest.class));
    }

    @Test
    void createHomeShouldReturnBadRequestWhenWebsiteIsInvalid() throws Exception {
        mockMvc.perform(post("/api/senior-home")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Dom Seniora",
                                  "phoneNumber": "123456789",
                                  "website": "niepoprawna-strona",
                                  "establishedDate": "2020-01-01",
                                  "address": {
                                    "city": "Warszawa",
                                    "street": "Polna",
                                    "number": "10",
                                    "postOffice": {
                                      "postalCode": "00-001",
                                      "city": "Warszawa"
                                    }
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateHomeShouldReturnUpdatedSeniorHomeResponse() throws Exception {
        SeniorHome seniorHome = seniorHome(1L, "Nowy Dom", "987654321", "nowy@dom.pl", null, address());
        when(seniorHomeService.updateHome(any(), eq(1L))).thenReturn(seniorHome);

        mockMvc.perform(patch("/api/senior-home/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nowy Dom",
                                  "phoneNumber": "987654321"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Nowy Dom"))
                .andExpect(jsonPath("$.phoneNumber").value("987654321"));

        verify(seniorHomeService).updateHome(any(), eq(1L));
    }

    @Test
    void updateHomeShouldReturnBadRequestWhenWebsiteIsInvalid() throws Exception {
        mockMvc.perform(patch("/api/senior-home/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "website": "niepoprawna-strona"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteHomeShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/senior-home/1"))
                .andExpect(status().isNoContent());

        verify(seniorHomeService).deleteHome(1L);
    }

    private static SeniorHome seniorHome(Long id, String name, String phoneNumber, String email, String website, Address address) {
        SeniorHome seniorHome = new SeniorHome();
        seniorHome.setId(id);
        seniorHome.setName(name);
        seniorHome.setPhoneNumber(phoneNumber);
        seniorHome.setEmail(email);
        seniorHome.setWebsite(website);
        seniorHome.setEstablishedDate(new Date());
        seniorHome.setAddress(address);
        return seniorHome;
    }

    private static Address address() {
        PostOffice postOffice = new PostOffice();
        postOffice.setPostalCode("00-001");
        postOffice.setCity("Warszawa");

        Address address = new Address();
        address.setCity("Warszawa");
        address.setStreet("Polna");
        address.setNumber("10");
        address.setPostOffice(postOffice);
        return address;
    }
}
