package com.screenvault.screenvaultAPI.post;

import java.util.Set;

public record GetPostsRequestBody (
    int page,
    int pageSize,
    String title,
    Set<String> tags
) {}
