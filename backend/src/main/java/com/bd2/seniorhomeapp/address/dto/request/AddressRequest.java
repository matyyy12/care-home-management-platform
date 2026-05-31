package com.bd2.seniorhomeapp.address.dto.request;

import com.bd2.seniorhomeapp.postOffice.dto.request.PostOfficeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressRequest (
    @NotNull
    @Size(max = 30)
    String city,

    @NotNull
    @Size(max = 30)
    String street,

    @NotNull
    @Size(max = 4)
    String number,

    @Valid
    @NotNull
    PostOfficeRequest postOffice
) {}
