package com.screenvault.screenvaultAPI.post;

public record PostPostRequestBody (
    Post post,
    boolean isPublic
) {}
