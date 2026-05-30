package com.bd2.seniorhomeapp.senior.dto;

import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;

public record SeniorResponse(
		Long id,
		String firstName,
		String lastName,
		String roomNumber,
		SeniorStatus status) {

	public static SeniorResponse from(Senior senior) {
		return new SeniorResponse(
				senior.getId(),
				senior.getFirstName(),
				senior.getLastName(),
				senior.getRoomNumber(),
				senior.getStatus());
	}
}
