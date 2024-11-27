package com.screenvault.screenvaultAPI.rating;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
            Principal principal
    ) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new RatingResponseBody("Sign in to post ratings.", false, null)
            );

        Rating rating = null;
        try {
            rating = ratingService.postRating(principal.getName(), requestBody.score(), requestBody.postId());
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
            Principal principal
    ) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new RatingResponseBody("Sign in to manage ratings.", false, null)
            );

        try {
            ratingService.deleteRating(principal.getName(), requestBody.postId());
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
