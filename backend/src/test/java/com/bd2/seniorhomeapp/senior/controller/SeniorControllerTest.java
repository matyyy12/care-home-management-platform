package com.bd2.seniorhomeapp.senior.controller;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bd2.seniorhomeapp.address.entity.Address;
import com.bd2.seniorhomeapp.address.repository.AddressRepository;
import com.bd2.seniorhomeapp.postOffice.entity.PostOffice;
import com.bd2.seniorhomeapp.postOffice.repository.PostOfficeRepository;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;
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

	@Autowired
	private SeniorRepository seniorRepository;

	private Long seniorHomeId;

	@BeforeEach
	void setUp() {
		seniorRepository.deleteAll();

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
						.with(authenticatedUser())
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

		mockMvc.perform(get("/api/seniors").with(authenticatedUser()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].firstName").value("Jan"))
				.andExpect(jsonPath("$[0].lastName").value("Kowalski"));
	}

	@Test
	void rejectsMissingFirstName() throws Exception {
		mockMvc.perform(post("/api/seniors")
						.with(authenticatedUser())
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "lastName": "Kowalski"
								}
								"""))
				.andExpect(status().isBadRequest());
	}

	private RequestPostProcessor authenticatedUser() {
    return com.bd2.seniorhomeapp.security.SecurityTestSupport.asManager(seniorHomeId);
}
}
