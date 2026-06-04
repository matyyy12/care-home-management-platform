package com.bd2.seniorhomeapp.authentication.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        AuthUserResponse user
) {
    public LoginResponse(String token, AuthUserResponse user) {
        this(token, "Bearer", user);
    }
}
