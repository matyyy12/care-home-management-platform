package com.bd2.seniorhomeapp.seniorHome.dto;

import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;

public record SeniorHomeSummaryResponse(Long id, String name) {

	public static SeniorHomeSummaryResponse from(SeniorHome seniorHome) {
		return new SeniorHomeSummaryResponse(seniorHome.getId(), seniorHome.getName());
	}
}
