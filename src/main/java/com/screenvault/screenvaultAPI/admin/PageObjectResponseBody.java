package com.screenvault.screenvaultAPI.admin;

import org.springframework.data.domain.Page;

public record PageObjectResponseBody(
        String message,
        boolean success,
        Page<?> objects
) {
}
