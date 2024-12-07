package com.screenvault.screenvaultAPI.post;

import org.springframework.data.domain.Page;

public record GetPostsResponseBody(
        String message,
        boolean success,
        Page<Post> posts
) {
}
