package com.screenvault.screenvaultAPI.comment;


import java.util.UUID;

public record DeleteCommentRequestBody(
        UUID postId,
        UUID commentId
) {
}
