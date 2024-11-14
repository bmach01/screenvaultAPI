package com.screenvault.screenvaultAPI.comment;

import org.bson.types.ObjectId;

public record PostCommentRequestBody(
        Comment comment,
        ObjectId postId
) {
}
