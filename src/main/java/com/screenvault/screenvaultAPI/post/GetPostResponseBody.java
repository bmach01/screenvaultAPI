package com.screenvault.screenvaultAPI.post;

public record GetPostResponseBody(
        String message,
        boolean success,
        Post posts
) {
}
