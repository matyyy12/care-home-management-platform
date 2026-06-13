package com.bd2.seniorhomeapp.admin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

import javax.sql.DataSource;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.admin.dto.response.AdminBackupResponse;
import com.bd2.seniorhomeapp.admin.dto.response.AdminConfigResponse;
import com.bd2.seniorhomeapp.admin.dto.response.AdminHealthResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final DataSource dataSource;
    private final Environment environment;

    @Override
    public AdminHealthResponse getHealth() {
        return new AdminHealthResponse(
                "UP",
                isDatabaseConnected(),
                environment.getProperty("spring.application.name", "backend"));
    }

    @Override
    public AdminConfigResponse getConfig() {
        return new AdminConfigResponse(
                environment.getProperty("app.jwt.expiration-seconds", Long.class, 86400L),
                environment.getProperty("app.sample-data.enabled", Boolean.class, true),
                environment.getProperty("app.cors.allowed-origins", "http://localhost:5173"));
    }

    @Override
    public AdminBackupResponse createBackup() {
        return new AdminBackupResponse(
                "Kopia zapasowa została zaplanowana (stub).",
                Instant.now().toString());
    }

    private boolean isDatabaseConnected() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (SQLException ex) {
            return false;
        }
    }
}
