package com.bd2.seniorhomeapp.postOffice.dto.request;

import jakarta.validation.constraints.Size;

public record PostOfficeUpdateRequest(
        @Size(max = 6)
        String postalCode,

        @Size(max = 30)
        String city
) {
}
