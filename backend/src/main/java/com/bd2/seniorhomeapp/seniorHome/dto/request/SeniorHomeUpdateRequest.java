package com.bd2.seniorhomeapp.seniorHome.dto.request;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record SeniorHomeUpdateRequest (
        @Size(max = 20)
        String name,

        @Size(max = 15)
        String phoneNumber,

        @Email
        @Size(max = 40)
        String email,

        @Size(max = 40)
        String website,

        Date establishedDate,

        @Valid
        AddressRequest address
) {
}
