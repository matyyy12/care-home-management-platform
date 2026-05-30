package com.bd2.seniorhomeapp.senior.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.senior.dto.CreateSeniorRequest;
import com.bd2.seniorhomeapp.senior.dto.SeniorResponse;
import com.bd2.seniorhomeapp.senior.dto.UpdateSeniorRequest;
import com.bd2.seniorhomeapp.senior.service.SeniorService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/seniors")
@AllArgsConstructor
public class SeniorController {

	private final SeniorService seniorService;

	@GetMapping
	public List<SeniorResponse> list() {
		return seniorService.findAll();
	}

	@PostMapping
	public ResponseEntity<SeniorResponse> create(@Valid @RequestBody CreateSeniorRequest request) {
		SeniorResponse created = seniorService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}")
	public SeniorResponse update(
			@PathVariable Long id,
			@Valid @RequestBody UpdateSeniorRequest request) {
		return seniorService.update(id, request);
	}
}
