package com.screenvault.screenvaultAPI.rating;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.post.Post;
import com.screenvault.screenvaultAPI.post.PostRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
        if (username == null) return;

        List<Rating> ratings = ratingRepository.findByPosterUsernameAndPostIdIn(
                username,
                posts.getContent().stream().map(Post::getId).toList()
        );
        if (ratings == null) return;

        Map<ObjectId, Rating> ratingsMap = ratings.stream()
                .collect(Collectors.toMap(Rating::getPostId, rating -> rating));

        posts.getContent().forEach(post -> {
            Rating rating = ratingsMap.get(post.getId());
            if (rating != null) post.setMyScore(rating.getRated());
        });
    }

    public Rating postRating(String token, Rating.Score score, ObjectId postId) throws Exception, InternalError {
        String username = jwtService.extractUsername(token);
        Rating rating = ratingRepository.findByPosterUsernameAndPostId(username, postId).orElse(null);
        Post post = postRepository.findById(postId).orElseThrow(() -> new Exception("Post does not exist."));

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
        } catch (Exception e) {
            throw new InternalError("Failed to save new rating. Try again later.");
        }

        return rating;
    }

    public void deleteRating(String token, ObjectId ratingId) throws Exception, InternalError {
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() -> new Exception("Rating does not exist."));
        Post post = postRepository.findById(rating.getPostId()).orElseThrow(() -> new Exception("Post does not exist."));
        String username = jwtService.extractUsername(token);

        if (!rating.getPosterUsername().equals(username)) throw new Exception("Rating is not principal's.");

        try {
            ratingRepository.delete(rating);
        } catch (Exception e) {
            throw new InternalError("Failed to delete the rating. Try again later.");
        }
    }
}
