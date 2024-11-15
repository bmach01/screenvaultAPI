package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.collection.Collection;
import com.screenvault.screenvaultAPI.collection.CollectionRepository;
import com.screenvault.screenvaultAPI.comment.CommentRepository;
import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.rating.RatingRepository;
import org.bson.types.ObjectId;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

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
        return postRepository.findAll(PageRequest.of(page, pageSize));
    }

    public Page<Post> getPostsByTitle(String title, int page, int pageSize) {
        return postRepository.findByTitleContaining(title, PageRequest.of(page, pageSize)).orElse(Page.empty());
    }

    public Page<Post> getPostsByTags(Set<String> tags, int page, int pageSize) {
        return postRepository.findByTagsIn(tags, PageRequest.of(page, pageSize)).orElse(Page.empty());
    }

    public Post uploadPost(Post post, boolean isPublic) throws InternalError, IllegalArgumentException {
        post.setPublic(isPublic);
        post.setComments(Collections.emptyList());
        Post savedPost = null;

        try {
            savedPost = postRepository.save(post);
        } catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        if (isPublic) {
            Collection globalCollection = collectionRepository.findByIsGlobal(true)
                    .orElseThrow(() -> new InternalError("Internal error. Try again later."));

            if (!addPostToCollection(savedPost.getId(), globalCollection.getId()))
                throw new InternalError("Failed to make post public.");
        }

        return savedPost;
    }

    public boolean addPostToCollection(ObjectId postId, ObjectId collectionId) {
        Query query = new Query(Criteria.where("id").is(collectionId));
        Update update = new Update().addToSet("posts", postId);
        return mongoTemplate.updateFirst(query, update, Collection.class).getModifiedCount() != 0;
    }
}
