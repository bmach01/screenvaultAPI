package com.screenvault.screenvaultAPI.admin;

import com.screenvault.screenvaultAPI.post.Post;

public record PatchPostResponseBody(
        String message,
        boolean success,
        Post post
) {
}
