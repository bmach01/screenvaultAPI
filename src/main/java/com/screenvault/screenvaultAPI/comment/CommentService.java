package com.screenvault.screenvaultAPI.comment;

import com.screenvault.screenvaultAPI.post.PostAsyncService;
import com.screenvault.screenvaultAPI.post.PostRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final PostAsyncService postAsyncService;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository,
            MongoTemplate mongoTemplate,
            PostAsyncService postAsyncService
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mongoTemplate = mongoTemplate;
        this.postAsyncService = postAsyncService;
    }

    public Page<Comment> getCommentsByPostId(UUID postId, int page, int pageSize)
            throws IllegalArgumentException
    {
        return commentRepository.findAllByPostId(postId, PageRequest.of(page, pageSize)).orElse(Page.empty());
    }


    public Comment uploadComment(String username, UUID postId, Comment comment)
            throws IllegalArgumentException, NoSuchElementException, InternalError
    {
        if (!postRepository.existsById(postId)) throw new NoSuchElementException("No post with that id exists.");

        Comment savedComment = null;
        comment.setUsername(username);
        comment.setPostId(postId);

        try {
            savedComment = commentRepository.save(comment);
            postAsyncService.incrementCommentCountAndSave(postId);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return savedComment;
    }

    public void userMarkCommentDeleted(String username, UUID commentId)
            throws IllegalArgumentException, PermissionDeniedDataAccessException, InternalError, NoSuchElementException
    {
        try {
            Comment comment = commentRepository.findById(commentId).orElseThrow();
            if (!comment.getUsername().equals(username))
                throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

            comment.setDeleted(true);
            commentRepository.save(comment);
            postAsyncService.decrementCommentCountAndSave(comment.getPostId());
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void markCommentDeletedByPost(UUID postId) throws RuntimeException {
        Query query = new Query(Criteria.where("postId").is(postId));
        Update update = new Update().set("isDeleted", true);
        mongoTemplate.updateMulti(query, update, Comment.class);
    }
}
