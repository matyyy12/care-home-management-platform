package com.bd2.seniorhomeapp.employee.dto.request;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;

public record EmployeeUpdateRequest(
        @Size(max = 30)
        String name,

        @Size(max = 30)
        String surname,

        @PositiveOrZero
        BigDecimal salary,

        @Email
        @Size(max = 40)
        String email,

        @Size(max = 15)
        String phone,

        Date birthDate,

        Date hireDate,

        Date fireDate,

        @Valid
        AddressRequest address,

        Long seniorHomeId,

        Long positionId,

        Long managerId,

        // TODO: Dodac po dolaczeniu brancha z encja Room.
        // Set<Long> roomIds,

        @Valid
        MedicalEmployeeRequest medicalEmployee
) {
}
