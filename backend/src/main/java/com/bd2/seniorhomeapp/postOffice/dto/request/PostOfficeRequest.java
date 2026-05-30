package com.bd2.seniorhomeapp.postOffice.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostOfficeRequest(
        @NotNull
        @Size(max = 6)
        String postalCode,

        @NotNull
        @Size(max = 30)
        String city
) {
}
