package com.screenvault.screenvaultAPI.admin;

import com.screenvault.screenvaultAPI.comment.Comment;

public record PatchCommentResponseBody(
        String message,
        boolean success,
        Comment comment
) {
}
