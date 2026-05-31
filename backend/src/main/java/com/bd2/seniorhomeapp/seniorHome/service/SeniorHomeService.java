package com.bd2.seniorhomeapp.seniorHome.service;

import com.bd2.seniorhomeapp.seniorHome.dto.request.SeniorHomeRequest;
import com.bd2.seniorhomeapp.seniorHome.dto.request.SeniorHomeUpdateRequest;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;

import java.util.List;

public interface SeniorHomeService {
    List<SeniorHome> findHomes(Long id);
    SeniorHome createHome(SeniorHomeRequest seniorHomeRequest);
    SeniorHome updateHome(SeniorHomeUpdateRequest seniorHomeUpdateRequest, Long id);
    void deleteHome(Long id);
}
