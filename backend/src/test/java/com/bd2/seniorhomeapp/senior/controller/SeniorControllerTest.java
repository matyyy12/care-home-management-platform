package com.bd2.seniorhomeapp.senior.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SeniorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createAndListSeniors() throws Exception {
		mockMvc.perform(post("/api/seniors")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "firstName": "Jan",
								  "lastName": "Kowalski",
								  "roomNumber": "12"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName").value("Jan"))
				.andExpect(jsonPath("$.lastName").value("Kowalski"))
				.andExpect(jsonPath("$.roomNumber").value("12"));

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
