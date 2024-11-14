package com.screenvault.screenvaultAPI.comment;

import org.bson.types.ObjectId;

public record DeleteCommentRequestBody(
        ObjectId postId,
        ObjectId commentId
) {
}
