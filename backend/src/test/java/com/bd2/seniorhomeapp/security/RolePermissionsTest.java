package com.bd2.seniorhomeapp.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.bd2.seniorhomeapp.authentication.entity.AppRole;

class RolePermissionsTest {

    @Test
    void employeeCanReadSeniorsButNotWrite() {
        assertThat(RolePermissions.canRead(AppRole.EMPLOYEE, AppModule.SENIORS)).isTrue();
        assertThat(RolePermissions.canWrite(AppRole.EMPLOYEE, AppModule.SENIORS)).isFalse();
    }

    @Test
    void adminTechCannotAccessSeniorsModule() {
        assertThat(RolePermissions.canRead(AppRole.ADMIN_TECH, AppModule.SENIORS)).isFalse();
        assertThat(RolePermissions.canWrite(AppRole.ADMIN_TECH, AppModule.SENIORS)).isFalse();
    }

    @Test
    void managerCanReadAndWriteSeniors() {
        assertThat(RolePermissions.canRead(AppRole.MANAGER, AppModule.SENIORS)).isTrue();
        assertThat(RolePermissions.canWrite(AppRole.MANAGER, AppModule.SENIORS)).isTrue();
    }
}
