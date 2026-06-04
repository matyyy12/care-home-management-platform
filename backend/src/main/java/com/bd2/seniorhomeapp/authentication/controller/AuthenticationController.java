package com.bd2.seniorhomeapp.authentication.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.authentication.dto.request.LoginRequest;
import com.bd2.seniorhomeapp.authentication.dto.response.AuthUserResponse;
import com.bd2.seniorhomeapp.authentication.dto.response.LoginResponse;
import com.bd2.seniorhomeapp.authentication.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @GetMapping("/me")
    public AuthUserResponse me(Principal principal) {
        return authenticationService.currentUser(principal.getName());
    }
}
