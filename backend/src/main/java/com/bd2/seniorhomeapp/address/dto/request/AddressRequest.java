package com.bd2.seniorhomeapp.address.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressRequest (
    @NotNull
    String city,

    @NotNull
    @Size(max = 30)
    String street,

    @NotNull
    @Size(max = 4)
    String number,

    @NotNull
    Long postOfficeId
) {}
