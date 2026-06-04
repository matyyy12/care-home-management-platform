package com.bd2.seniorhomeapp.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.bd2.seniorhomeapp.authentication.dto.request.LoginRequest;
import com.bd2.seniorhomeapp.authentication.dto.response.LoginResponse;
import com.bd2.seniorhomeapp.authentication.entity.AppRole;
import com.bd2.seniorhomeapp.authentication.entity.AuthUser;
import com.bd2.seniorhomeapp.authentication.repository.AuthUserRepository;
import com.bd2.seniorhomeapp.authentication.service.AuthenticationServiceImpl;
import com.bd2.seniorhomeapp.authentication.service.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void loginShouldReturnJwtAndUserWhenCredentialsAreValid() {
        AuthUser user = user();
        when(authUserRepository.findByEmailIgnoreCase("manager@seniorhouse.local")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "hash")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authenticationService.login(
                new LoginRequest("manager@seniorhouse.local", "Password123!")
        );

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user().email()).isEqualTo("manager@seniorhouse.local");
        assertThat(response.user().role()).isEqualTo(AppRole.MANAGER);
        assertThat(response.user().assignedSeniorHomeId()).isEqualTo(1L);
    }

    @Test
    void loginShouldRejectInvalidPassword() {
        AuthUser user = user();
        when(authUserRepository.findByEmailIgnoreCase("manager@seniorhouse.local")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad-password", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.login(
                new LoginRequest("manager@seniorhouse.local", "bad-password")
        )).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void currentUserShouldReturnAuthenticatedUserProfile() {
        AuthUser user = user();
        when(authUserRepository.findByEmailIgnoreCase("manager@seniorhouse.local")).thenReturn(Optional.of(user));

        assertThat(authenticationService.currentUser("manager@seniorhouse.local").role()).isEqualTo(AppRole.MANAGER);

        verify(authUserRepository).findByEmailIgnoreCase("manager@seniorhouse.local");
    }

    private AuthUser user() {
        AuthUser user = new AuthUser();
        user.setId(1L);
        user.setEmail("manager@seniorhouse.local");
        user.setPasswordHash("hash");
        user.setRole(AppRole.MANAGER);
        user.setAssignedSeniorHomeId(1L);
        return user;
    }
}
