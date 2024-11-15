package com.screenvault.screenvaultAPI.post;

public record UploadPostResponseBody(
        String message,
        boolean success,
        Post post
) {
}
