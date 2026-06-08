package com.bd2.seniorhomeapp.senior.dto;

import java.time.LocalDate;
import java.util.List;

import com.bd2.seniorhomeapp.senior.entity.Senior;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;

public record SeniorResponse(
		Long seniorId,
		String firstName,
		String lastName,
		LocalDate birthDate,
		String pesel,
		List<String> diseases,
		String gender,
		LocalDate accommodationDate,
		LocalDate dischargeDate,
		Long seniorHomeId,
		SeniorStatus status,
		Long roomId,
		String roomNumber) {

	public static SeniorResponse from(Senior senior) {
		return new SeniorResponse(
				senior.getSeniorId(),
				senior.getFirstName(),
				senior.getLastName(),
				senior.getBirthDate(),
				senior.getPesel(),
				senior.getDiseases(),
				senior.getGender(),
				senior.getAccommodationDate(),
				senior.getDischargeDate(),
				senior.getSeniorHome() != null ? senior.getSeniorHome().getId() : null,
				senior.getStatus(),
				senior.getRoom() != null ? senior.getRoom().getId() : null,
				senior.getRoom() != null ? senior.getRoom().getRoomNumber() : null);
	}
}
