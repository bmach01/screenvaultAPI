package com.screenvault.screenvaultAPI.authentication;

import com.screenvault.screenvaultAPI.user.User;

public record RegisterResponseBody(
    String message,
    User user
) {}
