package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.collection.Collection;
import com.screenvault.screenvaultAPI.collection.CollectionRepository;
import com.screenvault.screenvaultAPI.comment.CommentRepository;
import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.rating.Rating;
import com.screenvault.screenvaultAPI.rating.RatingRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    private static final String BEARER_PREFIX = "Bearer ";
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final CollectionRepository collectionRepository;
    private final JwtService jwtService;
    private final MongoTemplate mongoTemplate;
//    private final PostImageRepository postImageRepository;

    public PostService(
            PostRepository postRepository,
            CommentRepository commentRepository,
            RatingRepository ratingRepository,
            CollectionRepository collectionRepository,
            JwtService jwtService,
            MongoTemplate mongoTemplate
//            PostImageRepository postImageRepository
    ) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.ratingRepository = ratingRepository;
        this.collectionRepository = collectionRepository;
        this.jwtService = jwtService;
        this.mongoTemplate = mongoTemplate;
//        this.postImageRepository = postImageRepository;
    }

    public Page<Post> getLandingPagePostsPage(int page, int pageSize) {
        return postRepository.findByCreatedAtBetweenOrderByPopularityDesc(
                new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
                new Date(),
                PageRequest.of(page, pageSize)
        );
    }

    public Page<Post> getPostsByTitle(String title, int page, int pageSize) {
        return postRepository.findByTitleContaining(title, PageRequest.of(page, pageSize));
    }

    public Page<Post> getPostsByTags(Set<String> tags, int page, int pageSize) {
        return postRepository.findByTagsIn(tags, PageRequest.of(page, pageSize));
    }

    public Post savePost(Post post, boolean isPublic) {
        post.setPublic(isPublic);
        Post savedPost = null;

        // Check if failed to save
        try {
            savedPost = postRepository.save(post);
        } catch (Exception e) {
            return null;
        }

        if (isPublic) {
            Collection globalCollection = collectionRepository.findByIsGlobal(true);
            assert globalCollection != null;
            if (!addPostToCollection(savedPost.getId(), globalCollection.getId())) return null;
        }
        return savedPost;
    }

    public boolean addPostToCollection(ObjectId postId, ObjectId collectionId) {
        Query query = new Query(Criteria.where("id").is(collectionId));
        Update update = new Update().addToSet("posts", postId);
        return mongoTemplate.updateFirst(query, update, Collection.class).getModifiedCount() != 0;
    }

    public void addUserRatingToPosts(String token, Page<Post> posts) {
        String username = jwtService.extractUsername(token.substring(BEARER_PREFIX.length()));
        if (username == null) return;

        List<Rating> ratings = ratingRepository.findByPosterUsernameAndPostIdIn(
                username,
                posts.getContent().stream().map(Post::getId).toList()
        );
        if (ratings == null) return;

        Map<ObjectId, Rating> ratingsMap = ratings.stream()
                .collect(Collectors.toMap(Rating::getPostId, rating -> rating));

        posts.getContent().forEach(post -> post.setMyScore(ratingsMap.get(post.getId()).getRated()));
    }

}
