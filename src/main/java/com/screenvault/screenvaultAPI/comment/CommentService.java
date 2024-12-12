package com.screenvault.screenvaultAPI.comment;

import com.screenvault.screenvaultAPI.post.PostAsyncService;
import com.screenvault.screenvaultAPI.post.PostRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostAsyncService postAsyncService;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository,
            PostAsyncService postAsyncService
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.postAsyncService = postAsyncService;
    }

    public Page<Comment> getCommentsByPostId(UUID postId, int page, int pageSize) throws IllegalArgumentException {
        if (!postRepository.existsById(postId)) throw new IllegalArgumentException("Post of this id does not exist.");
        return commentRepository.findByPostId(postId, PageRequest.of(page, pageSize));
    }


    public Comment uploadComment(String username, UUID postId, Comment comment)
            throws IllegalArgumentException, InternalError
    {
        if (!postRepository.existsById(postId)) throw new IllegalArgumentException("No post with that id exists.");

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
}
