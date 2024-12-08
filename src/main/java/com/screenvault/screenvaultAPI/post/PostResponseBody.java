package com.screenvault.screenvaultAPI.post;

public record PostResponseBody(
        String message,
        boolean success,
        PostUserView post
) {
}
