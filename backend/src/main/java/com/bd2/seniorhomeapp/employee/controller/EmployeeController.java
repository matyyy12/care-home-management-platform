package com.bd2.seniorhomeapp.employee.controller;

import com.bd2.seniorhomeapp.employee.dto.request.EmployeeRequest;
import com.bd2.seniorhomeapp.employee.dto.request.EmployeeUpdateRequest;
import com.bd2.seniorhomeapp.employee.dto.response.EmployeeResponse;
import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.employee.entity.MedicalEmployee;
import com.bd2.seniorhomeapp.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<?> getEmployees(@RequestParam(required = false) Long id) {
        List<EmployeeResponse> response = employeeService.getEmployees(id)
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        Employee employee = employeeService.createEmployee(employeeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(employee));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody EmployeeUpdateRequest employeeUpdateRequest,
                                            @PathVariable Long id) {
        Employee employee = employeeService.updateEmployee(employeeUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(employee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        MedicalEmployee medicalEmployee = employee.getMedicalEmployee();

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getSurname(),
                employee.getSalary(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getBirthDate(),
                employee.getHireDate(),
                employee.getFireDate(),
                employee.getAddress().getId(),
                employee.getAddress().getCity(),
                employee.getAddress().getStreet(),
                employee.getAddress().getNumber(),
                employee.getAddress().getPostOffice().getPostalCode(),
                employee.getSeniorHome().getId(),
                employee.getPosition().getId(),
                employee.getPosition().getName(),
                employee.getManager() != null ? employee.getManager().getId() : null,
                employee.getSeniors().stream().map(senior -> senior.getSeniorId()).collect(Collectors.toSet()),
                employee.getActivities().stream().map(activity -> activity.getId()).collect(Collectors.toSet()),
                medicalEmployee != null ? medicalEmployee.getLicenseNumber() : null,
                medicalEmployee != null ? medicalEmployee.getSpecialty() : null
        );
    }
}
