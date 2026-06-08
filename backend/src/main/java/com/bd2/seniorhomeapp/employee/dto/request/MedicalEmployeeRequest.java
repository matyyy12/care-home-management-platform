package com.bd2.seniorhomeapp.employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicalEmployeeRequest(
        @NotBlank
        @Size(max = 20)
        String licenseNumber,

        @NotBlank
        @Size(max = 30)
        String specialty
) {
}
