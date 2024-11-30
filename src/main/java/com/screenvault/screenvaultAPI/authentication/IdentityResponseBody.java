package com.screenvault.screenvaultAPI.authentication;

import com.screenvault.screenvaultAPI.user.UserRole;

public record IdentityResponseBody(
        String message,
        boolean success,
        UserRole role
) {
}
