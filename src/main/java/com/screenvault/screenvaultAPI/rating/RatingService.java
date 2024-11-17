package com.screenvault.screenvaultAPI.rating;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.post.Post;
import com.screenvault.screenvaultAPI.post.PostRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final JwtService jwtService;
    private final PostRepository postRepository;

    public RatingService(RatingRepository ratingRepository, JwtService jwtService, PostRepository postRepository) {
        this.ratingRepository = ratingRepository;
        this.jwtService = jwtService;
        this.postRepository = postRepository;
    }

    public void addUserRatingToPosts(String token, Page<Post> posts) {
        String username = jwtService.extractUsername(token);
        List<Rating> ratings = ratingRepository.findByIdIn(
                posts.getContent().stream().map(post -> new RatingKey(post.getId(), username)).toList()
        );

        Map<UUID, Rating> ratingsMap = ratings.stream()
                .collect(Collectors.toMap(rating -> rating.getId().getPostId(), rating -> rating));

        posts.getContent().forEach(post -> {
            Rating rating = ratingsMap.get(post.getId());
            if (rating != null) post.setMyScore(rating.getRated());
        });
    }

    public Rating postRating(String token, Rating.Score score, UUID postId)
            throws InternalError, IllegalArgumentException, NoSuchElementException {
        String username = jwtService.extractUsername(token);
        Rating rating = ratingRepository.findById(new RatingKey(postId, username)).orElse(null);
        Post post = postRepository.findById(postId).orElseThrow();

        // update existing rating
        if (rating != null) {
            post.setScore(post.getScore() - rating.getRated().value + score.value);
            rating.setRated(score);
        }
        // create new rating
        else {
            rating = new Rating(null, score);
            rating.setId(new RatingKey(postId, username));
            post.setScore(post.getScore() + score.value);
        }

        try {
            postRepository.save(post);
            ratingRepository.save(rating);
        } catch (OptimisticLockingFailureException e) {
            throw new InternalError("Failed to save new rating. Try again later.");
        }

        return rating;
    }

    public void deleteRating(String token, UUID postId)
            throws PermissionDeniedDataAccessException, InternalError, IllegalArgumentException, NoSuchElementException {
        String username = jwtService.extractUsername(token);
        Rating rating = ratingRepository.findById(new RatingKey(postId, username)).orElseThrow();
        Post post = postRepository.findById(rating.getId().getPostId()).orElseThrow();

        if (!rating.getId().getUsername().equals(username))
            throw new PermissionDeniedDataAccessException("Rating is not principal's.", null);

        post.setScore(post.getScore() - rating.getRated().value);

        try {
            ratingRepository.delete(rating);
            postRepository.save(post);
        } catch (OptimisticLockingFailureException e) {
            throw new InternalError("Failed to delete the rating. Try again later.");
        }
    }
}
