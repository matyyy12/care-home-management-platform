package com.bd2.seniorhomeapp.senior.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSeniorRequest(
		@NotBlank String firstName,
		@NotBlank String lastName,
		@NotBlank String roomNumber) {
}
