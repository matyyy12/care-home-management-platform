package com.bd2.seniorhomeapp.authentication.dto.response;

import com.bd2.seniorhomeapp.authentication.entity.AppRole;
import com.bd2.seniorhomeapp.authentication.entity.AuthUser;

public record AuthUserResponse(
        Long id,
        String email,
        AppRole role,
        Long assignedSeniorHomeId
) {
    public static AuthUserResponse from(AuthUser user) {
        return new AuthUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getAssignedSeniorHomeId()
        );
    }
}
