package com.bd2.seniorhomeapp.employee.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

public record EmployeeResponse(
        Long id,
        String name,
        String surname,
        BigDecimal salary,
        String email,
        String phone,
        Date birthDate,
        Date hireDate,
        Date fireDate,
        Long addressId,
        String city,
        String street,
        String number,
        String postalCode,
        Long seniorHomeId,
        Long positionId,
        String positionName,
        Long managerId,
        Set<Long> seniorIds,
        Set<Long> activityIds,
        Set<Long> roomIds,
        String licenseNumber,
        String specialty
) {
}
