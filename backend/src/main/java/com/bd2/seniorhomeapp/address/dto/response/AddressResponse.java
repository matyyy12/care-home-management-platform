package com.bd2.seniorhomeapp.address.dto.response;


public record AddressResponse (
        Long id,
        String city,
        String street,
        String number,
        Long postOfficeId,
        String postalCode,
        String postOfficeCity
) {}