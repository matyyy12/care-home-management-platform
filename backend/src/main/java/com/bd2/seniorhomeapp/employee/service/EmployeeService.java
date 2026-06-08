package com.bd2.seniorhomeapp.employee.service;

import com.bd2.seniorhomeapp.employee.dto.request.EmployeeRequest;
import com.bd2.seniorhomeapp.employee.dto.request.EmployeeUpdateRequest;
import com.bd2.seniorhomeapp.employee.entity.Employee;

import java.util.List;
import java.util.Set;

public interface EmployeeService {
    List<Employee> getEmployees(Long id);
    Employee createEmployee(EmployeeRequest employeeRequest);
    Employee updateEmployee(EmployeeUpdateRequest employeeUpdateRequest, Long id);
    Employee assignEmployeeToRoom(Long employeeId, Long roomId);
    List<Employee> assignEmployeesToRoom(Set<Long> employeeIds, Long roomId);
    void deleteEmployee(Long id);
}
