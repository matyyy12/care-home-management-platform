package com.bd2.seniorhomeapp.authentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bd2.seniorhomeapp.authentication.entity.AuthUser;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
