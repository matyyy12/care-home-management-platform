package com.bd2.seniorhomeapp.senior.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bd2.seniorhomeapp.senior.dto.CreateSeniorRequest;
import com.bd2.seniorhomeapp.senior.dto.SeniorResponse;
import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SeniorServiceImpl implements SeniorService {

	private final SeniorRepository seniorRepository;

	@Override
	public List<SeniorResponse> findAll() {
		return seniorRepository.findAll().stream().map(SeniorResponse::from).toList();
	}

	@Override
	public SeniorResponse create(CreateSeniorRequest request) {
		if (request.firstName() == null || request.firstName().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "firstName is required");
		}
		if (request.lastName() == null || request.lastName().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lastName is required");
		}

		String roomNumber = request.roomNumber() != null && !request.roomNumber().isBlank()
				? request.roomNumber().trim()
				: null;

		Senior senior = new Senior(
				request.firstName().trim(),
				request.lastName().trim(),
				roomNumber);
		return SeniorResponse.from(seniorRepository.save(senior));
	}
}
