package com.screenvault.screenvaultAPI.post;

import java.util.UUID;

public record DeletePostRequestBody(
        UUID postId
) {
}
