package com.bd2.seniorhomeapp.address;

import com.bd2.seniorhomeapp.address.controller.AddressController;
import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.service.AddressService;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
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
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AddressController(addressService)).build();
    }

    @Test
    void getAllAddressesShouldReturnAddressResponses() throws Exception {
        Address address = address(1L, "Warszawa", "Polna", "10", postOffice(2L, "00-001", "Warszawa"));
        when(addressService.findAddresses(null, "Warszawa")).thenReturn(List.of(address));

        mockMvc.perform(get("/api/addresses").param("city", "Warszawa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].city").value("Warszawa"))
                .andExpect(jsonPath("$[0].street").value("Polna"))
                .andExpect(jsonPath("$[0].number").value("10"))
                .andExpect(jsonPath("$[0].postOfficeId").value(2))
                .andExpect(jsonPath("$[0].postalCode").value("00-001"))
                .andExpect(jsonPath("$[0].postOfficeCity").value("Warszawa"));

        verify(addressService).findAddresses(null, "Warszawa");
    }

    @Test
    void postAddressShouldReturnCreatedAddressResponse() throws Exception {
        Address address = address(1L, "Warszawa", "Polna", "10", postOffice(2L, "00-001", "Warszawa"));
        when(addressService.createAddress(any(AddressRequest.class))).thenReturn(address);

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "city": "Warszawa",
                                  "street": "Polna",
                                  "number": "10",
                                  "postOffice": {
                                    "postalCode": "00-001",
                                    "city": "Warszawa"
                                  }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.postOfficeId").value(2));

        verify(addressService).createAddress(any(AddressRequest.class));
    }

    @Test
    void patchAddressShouldReturnUpdatedAddressResponse() throws Exception {
        Address address = address(1L, "Krakow", "Polna", "15", postOffice(2L, "30-001", "Krakow"));
        when(addressService.updateAddress(any(), eq(1L))).thenReturn(address);

        mockMvc.perform(patch("/api/addresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "city": "Krakow",
                                  "number": "15",
                                  "postOffice": {
                                    "postalCode": "30-001",
                                    "city": "Krakow"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.city").value("Krakow"))
                .andExpect(jsonPath("$.number").value("15"));

        verify(addressService).updateAddress(any(), eq(1L));
    }

    @Test
    void deleteAddressShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/addresses/1"))
                .andExpect(status().isNoContent());

        verify(addressService).deleteAddress(1L);
    }

    private static Address address(Long id, String city, String street, String number, PostOffice postOffice) {
        Address address = new Address();
        address.setId(id);
        address.setCity(city);
        address.setStreet(street);
        address.setNumber(number);
        address.setPostOffice(postOffice);
        return address;
    }

    private static PostOffice postOffice(Long id, String postalCode, String city) {
        PostOffice postOffice = new PostOffice();
        postOffice.setId(id);
        postOffice.setPostalCode(postalCode);
        postOffice.setCity(city);
        return postOffice;
    }
}
