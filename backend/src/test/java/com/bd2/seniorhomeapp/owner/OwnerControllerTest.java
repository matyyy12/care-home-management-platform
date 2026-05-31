package com.bd2.seniorhomeapp.owner;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.owner.controller.OwnerController;
import com.bd2.seniorhomeapp.owner.dto.request.OwnerRequest;
import com.bd2.seniorhomeapp.owner.entity.Owner;
import com.bd2.seniorhomeapp.owner.service.OwnerService;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
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
class OwnerControllerTest {

    @Mock
    private OwnerService ownerService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OwnerController(ownerService)).build();
    }

    @Test
    void getAllOwnersShouldReturnOwnerResponses() throws Exception {
        Owner owner = owner(1L, "Jan", "Kowalski", seniorHome(2L), address());
        when(ownerService.getAllOwners(1L)).thenReturn(List.of(owner));

        mockMvc.perform(get("/api/owners").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Jan"))
                .andExpect(jsonPath("$[0].surname").value("Kowalski"))
                .andExpect(jsonPath("$[0].city").value("Warszawa"))
                .andExpect(jsonPath("$[0].street").value("Polna"))
                .andExpect(jsonPath("$[0].number").value("10"))
                .andExpect(jsonPath("$[0].postalCode").value("00-001"))
                .andExpect(jsonPath("$[0].seniorHomeId").value(2));

        verify(ownerService).getAllOwners(1L);
    }

    @Test
    void createOwnerShouldReturnCreatedOwnerResponse() throws Exception {
        Owner owner = owner(1L, "Jan", "Kowalski", seniorHome(2L), address());
        when(ownerService.createOwner(any(OwnerRequest.class))).thenReturn(owner);

        mockMvc.perform(post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Jan",
                                  "surname": "Kowalski",
                                  "seniorHomeId": 2,
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
                .andExpect(jsonPath("$.seniorHomeId").value(2));

        verify(ownerService).createOwner(any(OwnerRequest.class));
    }

    @Test
    void updateOwnerShouldReturnUpdatedOwnerResponse() throws Exception {
        Owner owner = owner(1L, "Adam", "Nowak", seniorHome(3L), address());
        when(ownerService.updateOwner(eq(1L), any())).thenReturn(owner);

        mockMvc.perform(patch("/api/owners/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Adam",
                                  "seniorHomeId": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Adam"))
                .andExpect(jsonPath("$.seniorHomeId").value(3));

        verify(ownerService).updateOwner(eq(1L), any());
    }

    @Test
    void deleteOwnerShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/owners/1"))
                .andExpect(status().isNoContent());

        verify(ownerService).deleteOwner(1L);
    }

    private static Owner owner(Long id, String name, String surname, SeniorHome seniorHome, Address address) {
        Owner owner = new Owner();
        owner.setId(id);
        owner.setName(name);
        owner.setSurname(surname);
        owner.setSeniorHome(seniorHome);
        owner.setAddress(address);
        return owner;
    }

    private static SeniorHome seniorHome(Long id) {
        SeniorHome seniorHome = new SeniorHome();
        seniorHome.setId(id);
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
