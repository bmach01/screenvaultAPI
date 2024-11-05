package com.screenvault.screenvaultAPI.post;

public record GetPostsRequestBody (
    int page,
    int pageSize,
    String title,
    String tag
) {}
