package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.comment.CommentRepository;
import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.rating.Rating;
import com.screenvault.screenvaultAPI.rating.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private static final String BEARER_PREFIX = "Bearer ";
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final JwtService jwtService;

    public PostService(PostRepository postRepository, CommentRepository commentRepository, RatingRepository ratingRepository, JwtService jwtService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.ratingRepository = ratingRepository;
        this.jwtService = jwtService;
    }

    public Page<Post> getLandingPagePostsPage(int page, int pageSize) {
        return postRepository.findByCreatedAtBetweenOrderByPopularityDesc(
            new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
            new Date(),
            PageRequest.of(page, pageSize)
        );
    }

    public void addUserRatingToPosts(String token, Page<Post> posts) {
        String username = jwtService.extractUsername(token.substring(BEARER_PREFIX.length()));
        if (username == null) return;

        List<Rating> ratings = ratingRepository.findByPosterUsernameAndPostIdIn(
            username,
            posts.getContent().stream().map(Post::getId).toList()
        );
        if (ratings == null) return;

        Map<UUID, Rating> ratingsMap = ratings.stream()
                .collect(Collectors.toMap(Rating::getPostId, rating -> rating));

        posts.getContent().forEach(post -> post.setMyScore(ratingsMap.get(post.getId()).getRated()));
    }

}
