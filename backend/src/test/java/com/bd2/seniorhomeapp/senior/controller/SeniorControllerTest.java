package com.bd2.seniorhomeapp.senior.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.repository.AddressRepository;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.postOffice.repository.PostOfficeRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;

@SpringBootTest
@AutoConfigureMockMvc
class SeniorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SeniorHomeRepository seniorHomeRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private PostOfficeRepository postOfficeRepository;

	private Long seniorHomeId;

	@BeforeEach
	void setUp() {
		PostOffice postOffice = postOfficeRepository.findByPostalCode("00-001")
				.orElseGet(() -> {
					PostOffice newPostOffice = new PostOffice();
					newPostOffice.setPostalCode("00-001");
					newPostOffice.setCity("Warszawa");
					return postOfficeRepository.save(newPostOffice);
				});

		Address address = new Address();
		address.setCity("Warszawa");
		address.setStreet("Polna");
		address.setNumber("10");
		address.setPostOffice(postOffice);
		address = addressRepository.save(address);

		SeniorHome seniorHome = new SeniorHome();
		seniorHome.setName("Dom Seniora");
		seniorHome.setPhoneNumber("123456789");
		seniorHome.setWebsite("https://example.com");
		seniorHome.setEstablishedDate(new Date());
		seniorHome.setAddress(address);
		seniorHomeId = seniorHomeRepository.save(seniorHome).getId();
	}

	@Test
	void createAndListSeniors() throws Exception {
		mockMvc.perform(post("/api/seniors")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "firstName": "Jan",
								  "lastName": "Kowalski",
								  "birthDate": "1950-01-15",
								  "pesel": "50011512345",
								  "diseases": ["diabetes"],
								  "gender": "M",
								  "accommodationDate": "2024-06-01",
								  "seniorHomeId": %d
								}
								""".formatted(seniorHomeId)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value("Jan"))
				.andExpect(jsonPath("$.lastName").value("Kowalski"))
				.andExpect(jsonPath("$.seniorHomeId").value(seniorHomeId.intValue()));

		mockMvc.perform(get("/api/seniors"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].firstName").value("Jan"));
	}

	@Test
	void rejectsMissingFirstName() throws Exception {
		mockMvc.perform(post("/api/seniors")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "lastName": "Kowalski"
								}
								"""))
				.andExpect(status().isBadRequest());
	}
}
