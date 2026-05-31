package com.bd2.seniorhomeapp.seniorHome.dto.request;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
        @Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*$")
        String website,

        Date establishedDate,

        @Valid
        AddressRequest address
) {
}
