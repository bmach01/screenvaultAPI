package com.screenvault.screenvaultAPI.rating;

import com.mongodb.lang.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/rating")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/postRating")
    public ResponseEntity<RatingResponseBody> postRating(
            @RequestBody PostNewRatingRequestBody requestBody,
            // JwtType.TOKEN.name()
            @Nullable @CookieValue("TOKEN") String token

    ) {
        Rating rating = null;
        try {
            rating = ratingService.postRating(token, requestBody.score(), requestBody.postId());
        } catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new RatingResponseBody(e.getMessage(), false, null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new RatingResponseBody(e.getMessage(), false, null)
            );
        }
        return ResponseEntity.ok(new RatingResponseBody(
                "Successfully posted rating.", true, rating)
        );
    }

    @DeleteMapping("/deletePost")
    public ResponseEntity<RatingResponseBody> deleteRating(
            @RequestBody DeleteRatingRequestBody requestBody,
            // JwtType.TOKEN.name()
            @Nullable @CookieValue("TOKEN") String token
    ) {
        try {
            ratingService.deleteRating(token, requestBody.ratingId());
        } catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new RatingResponseBody(e.getMessage(), false, null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new RatingResponseBody(e.getMessage(), false, null)
            );
        }
        return ResponseEntity.ok(new RatingResponseBody(
                "Successfully posted rating.", true, null)
        );
    }

}
