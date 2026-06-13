package com.bd2.seniorhomeapp.admin.dto.response;

public record AdminHealthResponse(
        String status,
        boolean databaseConnected,
        String applicationName) {
}
