package com.screenvault.screenvaultAPI.rating;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.post.Post;
import com.screenvault.screenvaultAPI.post.PostRepository;
import org.bson.types.ObjectId;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
        List<Rating> ratings = ratingRepository.findByPosterUsernameAndPostIdIn(
                username,
                posts.getContent().stream().map(Post::getId).toList()
        );

        Map<ObjectId, Rating> ratingsMap = ratings.stream()
                .collect(Collectors.toMap(Rating::getPostId, rating -> rating));

        posts.getContent().forEach(post -> {
            Rating rating = ratingsMap.get(post.getId());
            if (rating != null) post.setMyScore(rating.getRated());
        });
    }

    public Rating postRating(String token, Rating.Score score, ObjectId postId)
            throws InternalError, IllegalArgumentException, NoSuchElementException {
        String username = jwtService.extractUsername(token);
        Rating rating = ratingRepository.findByPosterUsernameAndPostId(username, postId).orElse(null);
        Post post = postRepository.findById(postId).orElseThrow();

        // update existing rating
        if (rating != null) {
            post.setScore(post.getScore() - rating.getRated().value + score.value);
            rating.setRated(score);
        }
        // create new rating
        else {
            rating = new Rating(null, username, postId, score);
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

    public void deleteRating(String token, ObjectId ratingId)
            throws PermissionDeniedDataAccessException, InternalError, IllegalArgumentException, NoSuchElementException {
        Rating rating = ratingRepository.findById(ratingId).orElseThrow();
        Post post = postRepository.findById(rating.getPostId()).orElseThrow();

        if (!rating.getPosterUsername().equals(jwtService.extractUsername(token)))
            throw new PermissionDeniedDataAccessException("Rating is not principal's.", null);

        try {
            ratingRepository.delete(rating);
            post.getComments().remove(ratingId);
            postRepository.save(post);
        } catch (OptimisticLockingFailureException e) {
            throw new InternalError("Failed to delete the rating. Try again later.");
        }
    }
}
