package com.bd2.seniorhomeapp.senior.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateSeniorRequest(
		@NotBlank String firstName,
		@NotBlank String lastName,
		@NotBlank String roomNumber) {
}
