package com.screenvault.screenvaultAPI.user;

public record UserUserView(
        String username,
        UserRole role
) {
    public UserUserView(User user) {
        this(user.getUsername(), user.getRole());
    }
}
