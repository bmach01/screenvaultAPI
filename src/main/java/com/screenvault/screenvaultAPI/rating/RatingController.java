package com.screenvault.screenvaultAPI.rating;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rating")
public class RatingController {

    @GetMapping("/getMyRatingForPosts")
    public ResponseEntity<Map<UUID, Rating.Score>> getMyRatingForPosts(
            @RequestHeader("Authorization") String requestAuthorizationHeader
    ) {
        return null;
    }

}
