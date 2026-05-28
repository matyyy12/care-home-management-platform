package com.bd2.seniorhomeapp.address.dto.request;


public record AddressUpdateRequest (
        String city,
        String street,
        String number,
        Long postOfficeId
) {}