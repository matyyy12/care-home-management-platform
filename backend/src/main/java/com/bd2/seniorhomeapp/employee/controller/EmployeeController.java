package com.bd2.seniorhomeapp.employee.controller;

import com.bd2.seniorhomeapp.employee.dto.request.EmployeeRequest;
import com.bd2.seniorhomeapp.employee.dto.request.EmployeeRoomAssignmentRequest;
import com.bd2.seniorhomeapp.employee.dto.request.EmployeeSeniorAssignmentRequest;
import com.bd2.seniorhomeapp.employee.dto.request.EmployeeUpdateRequest;
import com.bd2.seniorhomeapp.employee.dto.response.EmployeeResponse;
import com.bd2.seniorhomeapp.employee.entity.Employee;
import com.bd2.seniorhomeapp.employee.entity.MedicalEmployee;
import com.bd2.seniorhomeapp.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("@authz.canRead(T(com.bd2.seniorhomeapp.security.AppModule).EMPLOYEES)")
    public ResponseEntity<?> getEmployees(@RequestParam(required = false) Long id) {
        List<EmployeeResponse> response = employeeService.getEmployees(id)
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @PreAuthorize("@authz.canWrite(T(com.bd2.seniorhomeapp.security.AppModule).EMPLOYEES)")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        Employee employee = employeeService.createEmployee(employeeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(employee));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@authz.canWrite(T(com.bd2.seniorhomeapp.security.AppModule).EMPLOYEES)")
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody EmployeeUpdateRequest employeeUpdateRequest,
                                            @PathVariable Long id) {
        Employee employee = employeeService.updateEmployee(employeeUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(employee));
    }

    @PatchMapping("/{employeeId}/rooms/{roomId}")
    @PreAuthorize("@authz.canWrite(T(com.bd2.seniorhomeapp.security.AppModule).ASSIGNMENTS)")
    public ResponseEntity<?> assignEmployeeToRoom(@PathVariable Long employeeId,
                                                  @PathVariable Long roomId) {
        Employee employee = employeeService.assignEmployeeToRoom(employeeId, roomId);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(employee));
    }

    @PatchMapping("/rooms/{roomId}")
    @PreAuthorize("@authz.canWrite(T(com.bd2.seniorhomeapp.security.AppModule).ASSIGNMENTS)")
    public ResponseEntity<?> assignEmployeesToRoom(@Valid @RequestBody EmployeeRoomAssignmentRequest request,
                                                   @PathVariable Long roomId) {
        List<EmployeeResponse> response = employeeService.assignEmployeesToRoom(request.employeeIds(), roomId)
                .stream().map(this::mapToResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{employeeId}/rooms/{roomId}")
    @PreAuthorize("@authz.canWrite(T(com.bd2.seniorhomeapp.security.AppModule).ASSIGNMENTS)")
    public ResponseEntity<?> unassignEmployeeFromRoom(@PathVariable Long employeeId,
                                                      @PathVariable Long roomId) {
        Employee employee = employeeService.unassignEmployeeFromRoom(employeeId, roomId);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(employee));
    }

    @PatchMapping("/{employeeId}/seniors/{seniorId}")
    @PreAuthorize("@authz.canWrite(T(com.bd2.seniorhomeapp.security.AppModule).ASSIGNMENTS)")
    public ResponseEntity<?> assignSeniorToEmployee(@PathVariable Long employeeId,
                                                    @PathVariable Long seniorId) {
        Employee employee = employeeService.assignSeniorToEmployee(employeeId, seniorId);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(employee));
    }

    @DeleteMapping("/{employeeId}/seniors/{seniorId}")
    @PreAuthorize("@authz.canWrite(T(com.bd2.seniorhomeapp.security.AppModule).ASSIGNMENTS)")
    public ResponseEntity<?> unassignSeniorFromEmployee(@PathVariable Long employeeId,
                                                        @PathVariable Long seniorId) {
        Employee employee = employeeService.unassignSeniorFromEmployee(employeeId, seniorId);
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(employee));
    }

    @PatchMapping("/{employeeId}/seniors")
    @PreAuthorize("@authz.canWrite(T(com.bd2.seniorhomeapp.security.AppModule).ASSIGNMENTS)")
    public ResponseEntity<?> setEmployeeSeniors(@PathVariable Long employeeId,
                                                @Valid @RequestBody EmployeeSeniorAssignmentRequest request) {
        Employee employee = employeeService.setEmployeeSeniors(employeeId, request.seniorIds());
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(employee));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authz.canWrite(T(com.bd2.seniorhomeapp.security.AppModule).EMPLOYEES)")
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
                employee.getRooms().stream().map(room -> room.getId()).collect(Collectors.toSet()),
                medicalEmployee != null ? medicalEmployee.getLicenseNumber() : null,
                medicalEmployee != null ? medicalEmployee.getSpecialty() : null
        );
    }
}