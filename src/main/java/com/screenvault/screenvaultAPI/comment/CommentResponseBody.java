package com.screenvault.screenvaultAPI.comment;

public record CommentResponseBody(
        String message,
        boolean success,
        Comment comment
) {
}
