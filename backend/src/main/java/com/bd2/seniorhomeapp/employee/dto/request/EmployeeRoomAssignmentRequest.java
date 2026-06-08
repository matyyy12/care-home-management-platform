package com.bd2.seniorhomeapp.employee.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record EmployeeRoomAssignmentRequest(
        @NotEmpty
        Set<Long> employeeIds
) {
}
