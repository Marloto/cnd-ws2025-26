package de.thi.inf.cnd.rest.adapter.ingoing.rest.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT Service
 *
 * Simple JWT token validation service for the ingoing REST adapter.
 * Controllers inject this service and call validateAuthHeader() to check authentication.
 *
 * Example usage in controller:
 * <pre>
 * AuthenticatedUser user = jwtService.validateAuthHeader(authHeader);
 * if (user == null) {
 *     return ResponseEntity.status(401).body("Unauthorized");
 * }
 * // Use user.getUserId() to pass to domain
 * </pre>
 */
@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret:your-secret-key-change-in-production}") String secret) {
        // Create signing key from secret (must match auth service secret)
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validate Authorization header and extract user information
     *
     * Simple method to check authentication in controllers.
     * Returns null if authentication fails.
     *
     * @param authorizationHeader Authorization header from request (e.g., "Bearer xyz...")
     * @return AuthenticatedUser if valid, null if invalid/missing
     */
    public AuthenticatedUser validateAuthHeader(String authorizationHeader) {
        // Check if header exists and starts with "Bearer "
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        // Extract token (remove "Bearer " prefix)
        String token = authorizationHeader.substring(7);

        try {
            // Parse and validate token
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Extract user information from token
            String userId = claims.get("userId", String.class);
            String username = claims.get("username", String.class);

            return new AuthenticatedUser(userId, username);

        } catch (Exception e) {
            // Token is invalid or expired
            return null;
        }
    }
}
