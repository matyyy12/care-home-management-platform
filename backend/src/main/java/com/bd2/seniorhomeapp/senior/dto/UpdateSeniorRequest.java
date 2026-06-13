package com.bd2.seniorhomeapp.senior.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;

public record UpdateSeniorRequest(
		String firstName,
		String lastName,
		LocalDate birthDate,
		String pesel,
		List<String> diseases,
		String gender,
		LocalDate accommodationDate,
		LocalDate dischargeDate,
		Long seniorHomeId) {

	@AssertTrue(message = "Discharge date cannot be before birth date or accommodation date")
	public boolean isDischargeDateValid() {
		return SeniorDateValidation.isDischargeDateValid(dischargeDate, birthDate, accommodationDate);
	}
}
