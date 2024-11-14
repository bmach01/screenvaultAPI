package com.screenvault.screenvaultAPI.comment;

import org.bson.types.ObjectId;

public record GetCommentsRequestBody(
        int page,
        int pageSize,
        ObjectId postId
) {
}
