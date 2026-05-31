package com.bd2.seniorhomeapp.senior.dto;

import java.time.LocalDate;
import java.util.List;

import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;

import jakarta.validation.constraints.NotBlank;

public record CreateSeniorRequest(
		@NotBlank String firstName,
		@NotBlank String lastName,
		@NotBlank LocalDate birthDate,
		@NotBlank String pesel,
		@NotBlank List<String> diseases,
		@NotBlank String gender,
		@NotBlank LocalDate accommodationDate,
		@NotBlank LocalDate dischargeDate,
		@NotBlank SeniorHome seniorHome) {
}
