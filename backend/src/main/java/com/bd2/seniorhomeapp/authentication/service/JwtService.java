package com.bd2.seniorhomeapp.authentication.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bd2.seniorhomeapp.authentication.entity.AuthUser;

@Service
public class JwtService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Pattern SUBJECT_PATTERN = Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern EXPIRES_AT_PATTERN = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");

    private final byte[] secret;
    private final long expirationSeconds;
    private final Clock clock;

    @Autowired
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds:86400}") long expirationSeconds
    ) {
        this(secret, expirationSeconds, Clock.systemUTC());
    }

    JwtService(String secret, long expirationSeconds, Clock clock) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
        this.clock = clock;
    }

    public String generateToken(AuthUser user) {
        Instant now = Instant.now(clock);
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = """
                {"sub":"%s","role":"%s","iat":%d,"exp":%d}
                """.formatted(
                escapeJson(user.getEmail()),
                user.getRole().name(),
                now.getEpochSecond(),
                now.plusSeconds(expirationSeconds).getEpochSecond()
        ).trim();

        String unsignedToken = base64Url(header.getBytes(StandardCharsets.UTF_8))
                + "."
                + base64Url(payload.getBytes(StandardCharsets.UTF_8));

        return unsignedToken + "." + sign(unsignedToken);
    }

    public Optional<String> extractValidSubject(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Optional.empty();
            }

            String unsignedToken = parts[0] + "." + parts[1];
            if (!constantTimeEquals(sign(unsignedToken), parts[2])) {
                return Optional.empty();
            }

            String payload = decodePayload(parts);
            if (isExpired(payload)) {
                return Optional.empty();
            }

            return Optional.of(extractSubject(payload));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private String extractSubject(String payload) {
        Matcher matcher = SUBJECT_PATTERN.matcher(payload);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Token subject is missing");
        }
        return matcher.group(1);
    }

    private boolean isExpired(String payload) {
        Matcher matcher = EXPIRES_AT_PATTERN.matcher(payload);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Token expiration is missing");
        }

        long expiresAt = Long.parseLong(matcher.group(1));
        return Instant.now(clock).getEpochSecond() >= expiresAt;
    }

    private String decodePayload(String[] parts) {
        return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return base64Url(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot sign JWT", ex);
        }
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
