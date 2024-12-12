package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.domain.Page;

public record GetCommentsResponseBody(
        String message,
        boolean success,
        Page<CommentUserView> comments
) {
}
