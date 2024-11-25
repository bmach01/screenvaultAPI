package com.screenvault.screenvaultAPI.post;

import java.util.Set;
import java.util.UUID;

public record GetPostsRequestBody(
        int page,
        int pageSize,
        String title,
        Set<String> tags,
        UUID postId
) {
}
