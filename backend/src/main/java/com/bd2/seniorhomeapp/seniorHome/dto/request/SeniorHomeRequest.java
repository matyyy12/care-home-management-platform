package com.bd2.seniorhomeapp.seniorHome.dto.request;

import com.bd2.seniorhomeapp.address.dto.request.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Date;


public record SeniorHomeRequest (
        @NotBlank
        @Size(max = 20)
        String name,

        @NotBlank
        @Size(max = 15)
        String phoneNumber,

        @Email
        @Size(max = 40)
        String email,

        @Size(max = 40)
        @Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*$")
        String website,

        @NotNull
        Date establishedDate,

        @NotNull
        @Valid
        AddressRequest address
) {
}
