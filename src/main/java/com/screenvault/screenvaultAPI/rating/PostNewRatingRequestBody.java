package com.screenvault.screenvaultAPI.rating;

import java.util.UUID;

public record PostNewRatingRequestBody(
        UUID postId,
        Rating.Score score
) {
}
