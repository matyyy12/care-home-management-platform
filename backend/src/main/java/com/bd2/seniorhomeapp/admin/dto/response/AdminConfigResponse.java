package com.bd2.seniorhomeapp.admin.dto.response;

public record AdminConfigResponse(
        long jwtExpirationSeconds,
        boolean sampleDataEnabled,
        String corsAllowedOrigins) {
}
