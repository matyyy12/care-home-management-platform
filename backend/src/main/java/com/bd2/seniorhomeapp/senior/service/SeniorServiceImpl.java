package com.bd2.seniorhomeapp.senior.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.employee.repository.EmployeeRepository;
import com.bd2.seniorhomeapp.senior.dto.CreateSeniorRequest;
import com.bd2.seniorhomeapp.senior.dto.SeniorResponse;
import com.bd2.seniorhomeapp.senior.dto.UpdateSeniorRequest;
import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;
import com.bd2.seniorhomeapp.seniorHome.repository.SeniorHomeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class SeniorServiceImpl implements SeniorService {

	private final SeniorRepository seniorRepository;
	private final SeniorHomeRepository seniorHomeRepository;
	private final EmployeeRepository employeeRepository;

	@Override
	public List<SeniorResponse> findAll(Long id) {
		if (id != null) {
			Senior senior = seniorRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Senior not found"));
			return Stream.of(senior).map(this::mapToResponse).toList();
		}
		return seniorRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Override
	public SeniorResponse create(CreateSeniorRequest request) {
		Senior senior = new Senior(
				request.firstName().trim(),
				request.lastName().trim(),
				request.birthDate(),
				request.pesel().trim(),
				trimDiseases(request.diseases()),
				request.gender().trim(),
				request.accommodationDate(),
				request.dischargeDate(),
				findSeniorHome(request.seniorHomeId()));

		return mapToResponse(seniorRepository.save(senior));
	}

	@Override
	public SeniorResponse update(Long id, UpdateSeniorRequest request) {
		Senior senior = seniorRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Senior not found"));

		if (request.firstName() != null && !request.firstName().isBlank()) {
			senior.setFirstName(request.firstName().trim());
		}
		if (request.lastName() != null && !request.lastName().isBlank()) {
			senior.setLastName(request.lastName().trim());
		}
		if (request.birthDate() != null) {
			senior.setBirthDate(request.birthDate());
		}
		if (request.pesel() != null && !request.pesel().isBlank()) {
			senior.setPesel(request.pesel().trim());
		}
		if (request.diseases() != null) {
			senior.getDiseases().clear();
			senior.getDiseases().addAll(trimDiseases(request.diseases()));
		}
		if (request.gender() != null && !request.gender().isBlank()) {
			senior.setGender(request.gender().trim());
		}
		if (request.accommodationDate() != null) {
			senior.setAccommodationDate(request.accommodationDate());
		}
		if (request.dischargeDate() != null) {
			senior.setDischargeDate(request.dischargeDate());
		}
		if (request.seniorHomeId() != null) {
			senior.setSeniorHome(findSeniorHome(request.seniorHomeId()));
		}

		return mapToResponse(seniorRepository.save(senior));
	}

	private SeniorResponse mapToResponse(Senior senior) {
		List<Employee> guardians = employeeRepository.findBySeniors_SeniorId(senior.getSeniorId());
		List<Long> guardianIds = guardians.stream().map(Employee::getId).toList();
		List<String> guardianNames = guardians.stream()
				.map(employee -> employee.getName() + " " + employee.getSurname())
				.toList();
		return SeniorResponse.from(senior, guardianIds, guardianNames);
	}

	private SeniorHome findSeniorHome(Long id) {
		return seniorHomeRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Senior home not found"));
	}

	private List<String> trimDiseases(List<String> diseases) {
		return diseases.stream()
				.map(String::trim)
				.filter(disease -> !disease.isBlank())
				.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
}
