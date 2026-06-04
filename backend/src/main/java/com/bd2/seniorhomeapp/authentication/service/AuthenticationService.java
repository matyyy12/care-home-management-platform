package com.bd2.seniorhomeapp.authentication.service;

import com.bd2.seniorhomeapp.authentication.dto.request.LoginRequest;
import com.bd2.seniorhomeapp.authentication.dto.response.AuthUserResponse;
import com.bd2.seniorhomeapp.authentication.dto.response.LoginResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest request);

    AuthUserResponse currentUser(String email);
}
