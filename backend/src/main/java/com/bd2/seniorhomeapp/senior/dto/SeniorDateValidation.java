package com.bd2.seniorhomeapp.senior.dto;

import java.time.LocalDate;

final class SeniorDateValidation {

	private SeniorDateValidation() {
	}

	static boolean isDischargeDateValid(LocalDate dischargeDate, LocalDate birthDate, LocalDate accommodationDate) {
		if (dischargeDate == null) {
			return true;
		}
		if (birthDate != null && dischargeDate.isBefore(birthDate)) {
			return false;
		}
		return accommodationDate == null || !dischargeDate.isBefore(accommodationDate);
	}
}
