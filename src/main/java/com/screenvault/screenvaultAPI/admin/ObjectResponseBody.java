package com.screenvault.screenvaultAPI.admin;

import org.springframework.data.domain.Page;

public record ObjectResponseBody(
        String message,
        boolean success,
        Page<?> objects
) {
}
