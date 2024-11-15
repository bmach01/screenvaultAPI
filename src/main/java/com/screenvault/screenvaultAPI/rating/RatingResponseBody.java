package com.screenvault.screenvaultAPI.rating;

public record RatingResponseBody(
        String message,
        Boolean success,
        Rating rating
) {
}
