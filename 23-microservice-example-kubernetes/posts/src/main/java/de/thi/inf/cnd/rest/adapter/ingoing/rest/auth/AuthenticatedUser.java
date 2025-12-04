package de.thi.inf.cnd.rest.adapter.ingoing.rest.auth;

/**
 * Authenticated User
 *
 * Simple object to hold authenticated user information from JWT token.
 * Used to pass user context from REST adapter to domain layer.
 */
public class AuthenticatedUser {

    private final String userId;
    private final String username;

    public AuthenticatedUser(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "AuthenticatedUser{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
