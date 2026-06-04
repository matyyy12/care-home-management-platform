package com.bd2.seniorhomeapp.authentication.service;

import com.bd2.seniorhomeapp.authentication.dto.request.LoginRequest;
import com.bd2.seniorhomeapp.authentication.dto.response.AuthUserResponse;
import com.bd2.seniorhomeapp.authentication.dto.response.LoginResponse;
import com.bd2.seniorhomeapp.authentication.entity.AuthUser;
import com.bd2.seniorhomeapp.authentication.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        AuthUser user = authUserRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return new LoginResponse(jwtService.generateToken(user), AuthUserResponse.from(user));
    }

    @Override
    public AuthUserResponse currentUser(String email) {
        return authUserRepository.findByEmailIgnoreCase(email)
                .map(AuthUserResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
    }
}
