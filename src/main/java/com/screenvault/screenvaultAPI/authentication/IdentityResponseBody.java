package com.screenvault.screenvaultAPI.authentication;

import com.screenvault.screenvaultAPI.user.User;

public record IdentityResponseBody(
        String message,
        boolean success,
        User user
) {
}
