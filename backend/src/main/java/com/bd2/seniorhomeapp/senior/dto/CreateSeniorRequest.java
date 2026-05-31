package com.bd2.seniorhomeapp.senior.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSeniorRequest(
		@NotBlank String firstName,
		@NotBlank String lastName,
		@NotNull LocalDate birthDate,
		@NotBlank String pesel,
		@NotNull List<String> diseases,
		@NotBlank String gender,
		@NotNull LocalDate accommodationDate,
		LocalDate dischargeDate,
		@NotNull Long seniorHomeId) {
}
