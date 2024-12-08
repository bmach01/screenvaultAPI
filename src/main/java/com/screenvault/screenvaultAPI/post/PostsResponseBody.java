package com.screenvault.screenvaultAPI.post;

import org.springframework.data.domain.Page;

public record PostsResponseBody(
        String message,
        boolean success,
        Page<PostUserView> posts
) {
}
