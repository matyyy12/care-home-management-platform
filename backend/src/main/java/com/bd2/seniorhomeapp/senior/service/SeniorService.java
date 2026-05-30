package com.bd2.seniorhomeapp.senior.service;

import java.util.List;

import com.bd2.seniorhomeapp.senior.dto.CreateSeniorRequest;
import com.bd2.seniorhomeapp.senior.dto.SeniorResponse;

public interface SeniorService {

	List<SeniorResponse> findAll();

	SeniorResponse create(CreateSeniorRequest request);
}
