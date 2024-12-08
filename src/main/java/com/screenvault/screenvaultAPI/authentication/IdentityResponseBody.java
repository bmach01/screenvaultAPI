package com.screenvault.screenvaultAPI.authentication;

import com.screenvault.screenvaultAPI.user.UserUserView;

public record IdentityResponseBody(
        String message,
        boolean success,
        UserUserView user
) {
}
