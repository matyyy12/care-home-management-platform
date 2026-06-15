package com.bd2.seniorhomeapp.security;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationService {

    private final CurrentUser currentUser;

    public boolean canRead(AppModule module) {
        return RolePermissions.canRead(currentUser.role(), module);
    }

    public boolean canWrite(AppModule module) {
        return RolePermissions.canWrite(currentUser.role(), module);
    }
}
