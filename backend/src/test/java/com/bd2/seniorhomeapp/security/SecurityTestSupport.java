package com.bd2.seniorhomeapp.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class SecurityTestSupport {
    
    private SecurityTestSupport() {}

    public static RequestPostProcessor asRole(String role, Long assignedSeniorHomeId) {
        return jwt().jwt(builder -> {
            builder.claim("role", role);
            if (assignedSeniorHomeId != null) {
                builder.claim("assignedSeniorHomeId", assignedSeniorHomeId);
            }
        }).authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public static RequestPostProcessor asDirector() {
        return asRole("DIRECTOR", null);
    }

    public static RequestPostProcessor asManager(Long homeId) {
        return asRole("MANAGER", homeId);
    }

    public static RequestPostProcessor asEmployee(Long homeId) {
        return asRole("EMPLOYEE", homeId);
    }

    public static RequestPostProcessor asMedical(Long homeId) {
        return asRole("MEDICAL", homeId);
    }

    public static RequestPostProcessor asAdminTech() {
        return asRole("ADMIN_TECH", null);
    }
}