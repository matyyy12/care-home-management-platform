package com.bd2.seniorhomeapp.postOffice.dto.response;


public record PostOfficeResponse(
        Long id,
        String postalCode,
        String city
) {
}
