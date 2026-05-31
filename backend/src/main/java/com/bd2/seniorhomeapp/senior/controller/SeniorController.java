package com.bd2.seniorhomeapp.senior.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<?> list(@RequestParam(required = false) Long id) {
		List<SeniorResponse> response = seniorService.findAll(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping
	public ResponseEntity<SeniorResponse> create(@Valid @RequestBody CreateSeniorRequest request) {
		SeniorResponse created = seniorService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(
			@PathVariable Long id,
			@Valid @RequestBody UpdateSeniorRequest request) {
		SeniorResponse seniorResponse = seniorService.update(id, request);
		return ResponseEntity.status(HttpStatus.OK).body(seniorResponse);
	}
}
