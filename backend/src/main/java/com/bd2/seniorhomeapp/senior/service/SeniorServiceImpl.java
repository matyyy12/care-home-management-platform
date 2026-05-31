package com.bd2.seniorhomeapp.senior.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bd2.seniorhomeapp.senior.dto.CreateSeniorRequest;
import com.bd2.seniorhomeapp.senior.dto.SeniorResponse;
import com.bd2.seniorhomeapp.senior.dto.UpdateSeniorRequest;
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
		Senior senior = new Senior(
				request.firstName().trim(),
				request.lastName().trim(),
				request.birthDate(),
				request.pesel().trim(),
				request.diseases().stream().map(String::trim).toList(),
				request.gender().trim(),
				request.accommodationDate(),
				request.dischargeDate(),
				request.seniorHome());
		return SeniorResponse.from(seniorRepository.save(senior));
	}

	@Override
	public SeniorResponse update(Long id, UpdateSeniorRequest request) {
		Senior senior = seniorRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Senior not found"));

		senior.setFirstName(request.firstName().trim());
		senior.setLastName(request.lastName().trim());
		senior.setBirthDate(request.birthDate());
		senior.setPesel(request.pesel().trim());
		senior.setDiseases(request.diseases().stream().map(String::trim).toList());
		senior.setGender(request.gender().trim());
		senior.setAccommodationDate(request.accommodationDate());
		senior.setDischargeDate(request.dischargeDate());
		senior.setSeniorHome(request.seniorHome());

		return SeniorResponse.from(seniorRepository.save(senior));
	}
}
