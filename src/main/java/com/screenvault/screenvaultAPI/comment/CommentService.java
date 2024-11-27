package com.screenvault.screenvaultAPI.comment;

import com.screenvault.screenvaultAPI.post.Post;
import com.screenvault.screenvaultAPI.post.PostRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public Page<Comment> getCommentsByPostId(UUID postId, int page, int pageSize)
            throws IllegalArgumentException, NoSuchElementException
    {
        // TODO: get comments ONLY under post by post id
        Post post = postRepository.findById(postId).orElseThrow();

        return commentRepository.findByIdIn(post.getComments(), PageRequest.of(page, pageSize)).orElse(Page.empty());
    }


    public Comment uploadComment(String username, UUID postId, Comment comment)
            throws IllegalArgumentException, NoSuchElementException, InternalError
    {
        Comment savedComment = null;
        comment.setPostedOn(new Date());
        comment.setUsername(username);

        Post post = null;

        post = postRepository.findById(postId).orElseThrow();

        // TODO: add transaction
        try {
            savedComment = commentRepository.save(comment);

            post.getComments().addLast(savedComment.getId()); // Post::getComments should not return null
            postRepository.save(post);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return savedComment;
    }

    public void deleteComment(String username, UUID postId, UUID commentId)
            throws IllegalArgumentException, PermissionDeniedDataAccessException, InternalError, NoSuchElementException
    {
        Comment comment = null;
        Post post = null;

        try {
            comment = commentRepository.findById(commentId).orElseThrow();
            if (!comment.getUsername().equals(username))
                throw new PermissionDeniedDataAccessException("Collection is not principal's.", null);

            post = postRepository.findById(postId).orElseThrow();
            commentRepository.deleteById(commentId);
            post.getComments().remove(commentId);
            postRepository.save(post);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }
}
