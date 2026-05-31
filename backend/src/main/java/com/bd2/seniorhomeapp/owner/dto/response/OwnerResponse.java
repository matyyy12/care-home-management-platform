package com.bd2.seniorhomeapp.owner.dto.response;


public record OwnerResponse (
        Long id,
        String name,
        String surname,

        String city,
        String street,
        String number,
        String postalCode,

        Long seniorHomeId
){
}
