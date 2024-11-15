package com.screenvault.screenvaultAPI.rating;

import org.bson.types.ObjectId;

public record PostNewRatingRequestBody(
        ObjectId postId,
        Rating.Score score
) {
}
