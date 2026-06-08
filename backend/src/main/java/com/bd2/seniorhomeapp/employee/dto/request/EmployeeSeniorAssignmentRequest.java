package com.bd2.seniorhomeapp.employee.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record EmployeeSeniorAssignmentRequest(@NotNull Set<Long> seniorIds) {
}
