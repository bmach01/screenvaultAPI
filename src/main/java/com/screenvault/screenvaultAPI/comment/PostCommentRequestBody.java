package com.screenvault.screenvaultAPI.comment;

import java.util.UUID;

public record PostCommentRequestBody(
        Comment comment,
        UUID postId
) {
}
