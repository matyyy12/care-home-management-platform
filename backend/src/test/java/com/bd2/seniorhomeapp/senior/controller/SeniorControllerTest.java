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

import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;

@SpringBootTest
@AutoConfigureMockMvc
class SeniorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SeniorHomeRepository seniorHomeRepository;

	private Long seniorHomeId;

	@BeforeEach
	void setUp() {
		SeniorHome seniorHome = new SeniorHome();
		seniorHome.setName("Dom Seniora");
		seniorHome.setPhoneNumber("123456789");
		seniorHome.setWebsite("https://example.com");
		seniorHome.setEstablishedDate(new Date());
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
