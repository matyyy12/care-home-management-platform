package com.bd2.seniorhomeapp.seniorHome.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.seniorHome.dto.SeniorHomeSummaryResponse;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/senior-homes")
@AllArgsConstructor
public class SeniorHomeController {

	private final SeniorHomeRepository seniorHomeRepository;

	@GetMapping
	public List<SeniorHomeSummaryResponse> list() {
		return seniorHomeRepository.findAll().stream()
				.map(SeniorHomeSummaryResponse::from)
				.toList();
	}
}
