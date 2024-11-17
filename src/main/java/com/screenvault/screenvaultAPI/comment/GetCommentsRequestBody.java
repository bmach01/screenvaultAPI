package com.screenvault.screenvaultAPI.comment;

import java.util.UUID;

public record GetCommentsRequestBody(
        int page,
        int pageSize,
        UUID postId
) {
}
