package com.screenvault.screenvaultAPI.rating;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


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
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token

    ) {
        Rating rating = null;

        try {
            rating = ratingService.postRating(token, requestBody.score(), requestBody.postId());
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new RatingResponseBody(e.getMessage(), false, null)
            );
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new RatingResponseBody(e.getMessage(), false, null)
            );
        }
        return ResponseEntity.ok(new RatingResponseBody(
                "Successfully posted rating.", true, rating)
        );
    }

    @DeleteMapping("/deleteRating")
    public ResponseEntity<RatingResponseBody> deleteRating(
            @RequestBody DeleteRatingRequestBody requestBody,
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token
    ) {
        try {
            ratingService.deleteRating(token, requestBody.postId());
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new RatingResponseBody(e.getMessage(), false, null)
            );
        }
        catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(
                    new RatingResponseBody(e.getMessage(), false, null)
            );
        }
        catch (PermissionDeniedDataAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new RatingResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(new RatingResponseBody(
                "Successfully deleted rating.", true, null)
        );
    }

}
