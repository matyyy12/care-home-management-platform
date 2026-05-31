package com.bd2.seniorhomeapp.seniorHome.dto.response;

import jakarta.annotation.Nullable;

public record SeniorHomeResponse(
        Long id,
        String name,
        String phoneNumber,
        @Nullable
        String email,
        @Nullable
        String website,

        String city,
        String street,
        String number
) {
}
