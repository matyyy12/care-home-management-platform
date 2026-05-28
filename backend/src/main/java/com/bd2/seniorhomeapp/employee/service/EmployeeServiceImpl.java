package com.bd2.seniorhomeapp.employee.service;

import com.bd2.seniorhomeapp.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmlpoyeeService{
    private final EmployeeRepository employeeRepository;



}
