package com.bd2.seniorhomeapp.security;

import com.bd2.seniorhomeapp.authentication.entity.AppRole;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    
    private Jwt getJwt() {
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public AppRole role() {
        String roleClaim = getJwt().getClaimAsString("role");
        return AppRole.valueOf(roleClaim.toUpperCase()); 
    }

    
    public Long assignedSeniorHomeId() {
        return getJwt().getClaim("assignedSeniorHomeId");
    }

    public boolean isHomeScoped() {
        AppRole currentRole = role();
        return currentRole == AppRole.MANAGER || 
               currentRole == AppRole.EMPLOYEE || 
               currentRole == AppRole.MEDICAL;
    }
}