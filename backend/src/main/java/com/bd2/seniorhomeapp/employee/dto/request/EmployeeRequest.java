package com.bd2.seniorhomeapp.employee.dto.request;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;

public record EmployeeRequest(
        @NotBlank
        @Size(max = 30)
        String name,

        @NotBlank
        @Size(max = 30)
        String surname,

        @NotNull
        @PositiveOrZero
        BigDecimal salary,

        @Email
        @Size(max = 40)
        String email,

        @Size(max = 15)
        String phone,

        @NotNull
        Date birthDate,

        @NotNull
        Date hireDate,

        Date fireDate,

        @NotNull
        @Valid
        AddressRequest address,

        @NotNull
        Long seniorHomeId,

        @NotNull
        Long positionId,

        Long managerId,

        @Valid
        MedicalEmployeeRequest medicalEmployee
) {
}
