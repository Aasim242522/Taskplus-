package com.taskmonitor.module.auth.dto;

import java.util.UUID;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UserResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String refreshToken, String tokenType, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.user = user;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public static class Builder {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private UserResponse user;

        public Builder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public Builder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder user(UserResponse user) { this.user = user; return this; }
        public AuthResponse build() {
            return new AuthResponse(accessToken, refreshToken, tokenType, user);
        }
    }

    public static class UserResponse {
        private UUID id;
        private String email;
        private String fullName;
        private String timezone;
        private String role;

        public UserResponse() {
        }

        public UserResponse(UUID id, String email, String fullName, String timezone, String role) {
            this.id = id;
            this.email = email;
            this.fullName = fullName;
            this.timezone = timezone;
            this.role = role;
        }

        public static UserResponseBuilder builder() {
            return new UserResponseBuilder();
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public static class UserResponseBuilder {
            private UUID id;
            private String email;
            private String fullName;
            private String timezone;
            private String role;

            public UserResponseBuilder id(UUID id) { this.id = id; return this; }
            public UserResponseBuilder email(String email) { this.email = email; return this; }
            public UserResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
            public UserResponseBuilder timezone(String timezone) { this.timezone = timezone; return this; }
            public UserResponseBuilder role(String role) { this.role = role; return this; }
            public UserResponse build() {
                return new UserResponse(id, email, fullName, timezone, role);
            }
        }
    }
}
