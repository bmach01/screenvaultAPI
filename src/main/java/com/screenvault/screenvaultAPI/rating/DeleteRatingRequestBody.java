package com.screenvault.screenvaultAPI.rating;

import org.bson.types.ObjectId;

public record DeleteRatingRequestBody(
        ObjectId postId
) {
}
