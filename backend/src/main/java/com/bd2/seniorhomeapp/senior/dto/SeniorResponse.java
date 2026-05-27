package com.bd2.seniorhomeapp.senior.dto;

import com.bd2.seniorhomeapp.senior.entity.Senior;

public record SeniorResponse(Long id, String firstName, String lastName, String roomNumber) {

	public static SeniorResponse from(Senior senior) {
		return new SeniorResponse(
				senior.getId(),
				senior.getFirstName(),
				senior.getLastName(),
				senior.getRoomNumber());
	}
}
