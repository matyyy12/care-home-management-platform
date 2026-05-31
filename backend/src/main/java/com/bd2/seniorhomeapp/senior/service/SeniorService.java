package com.bd2.seniorhomeapp.senior.service;

import java.util.List;

import com.bd2.seniorhomeapp.senior.dto.CreateSeniorRequest;
import com.bd2.seniorhomeapp.senior.dto.SeniorResponse;
import com.bd2.seniorhomeapp.senior.dto.UpdateSeniorRequest;

public interface SeniorService {
	List<SeniorResponse> findAll(Long id);

	SeniorResponse create(CreateSeniorRequest request);

	SeniorResponse update(Long id, UpdateSeniorRequest request);
}
