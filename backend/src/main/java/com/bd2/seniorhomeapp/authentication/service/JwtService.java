package com.bd2.seniorhomeapp.authentication.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.authentication.entity.AuthUser;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;
    private final Clock clock;

    @Autowired
    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.expiration-seconds:86400}") long expirationSeconds
    ) {
        this(jwtEncoder, expirationSeconds, Clock.systemUTC());
    }

    JwtService(JwtEncoder jwtEncoder, long expirationSeconds, Clock clock) {
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
        this.clock = clock;
    }

    public String generateToken(AuthUser user) {
        Instant now = Instant.now(clock);
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .claim("role", user.getRole().name());

        if (user.getAssignedSeniorHomeId() != null) {
            claims.claim("assignedSeniorHomeId", user.getAssignedSeniorHomeId());
        }

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}
