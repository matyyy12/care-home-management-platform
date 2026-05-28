package com.bd2.seniorhomeapp.postOffice.dto.request;


public record PostOfficeUpdateRequest(
        String postalCode,
        String city
) {
}