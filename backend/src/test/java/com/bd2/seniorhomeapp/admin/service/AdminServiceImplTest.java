package com.bd2.seniorhomeapp.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

class AdminServiceImplTest {

    @Test
    void getHealthShouldReportDatabaseStatus() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);

        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.application.name", "backend");

        AdminServiceImpl service = new AdminServiceImpl(dataSource, environment);

        var health = service.getHealth();

        assertThat(health.status()).isEqualTo("UP");
        assertThat(health.databaseConnected()).isTrue();
        assertThat(health.applicationName()).isEqualTo("backend");
    }

    @Test
    void getConfigShouldReadEnvironmentProperties() {
        DataSource dataSource = mock(DataSource.class);
        Environment environment = new MockEnvironment()
                .withProperty("app.jwt.expiration-seconds", "3600")
                .withProperty("app.sample-data.enabled", "false")
                .withProperty("app.cors.allowed-origins", "http://localhost:5173");

        AdminServiceImpl service = new AdminServiceImpl(dataSource, environment);

        var config = service.getConfig();

        assertThat(config.jwtExpirationSeconds()).isEqualTo(3600L);
        assertThat(config.sampleDataEnabled()).isFalse();
        assertThat(config.corsAllowedOrigins()).isEqualTo("http://localhost:5173");
    }

    @Test
    void createBackupShouldReturnStubResponse() {
        AdminServiceImpl service = new AdminServiceImpl(mock(DataSource.class), new MockEnvironment());

        var backup = service.createBackup();

        assertThat(backup.message()).contains("stub");
        assertThat(backup.timestamp()).isNotBlank();
    }
}
