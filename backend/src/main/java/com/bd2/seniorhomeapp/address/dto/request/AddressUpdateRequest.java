package com.bd2.seniorhomeapp.address.dto.request;

import jakarta.validation.constraints.Size;

public record AddressUpdateRequest (
        @Size(max = 30)
        String city,

        @Size(max = 30)
        String street,

        @Size(max = 4)
        String number,

        Long postOfficeId
) {}
