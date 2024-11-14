package com.screenvault.screenvaultAPI.comment;

import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.post.Post;
import com.screenvault.screenvaultAPI.post.PostRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final JwtService jwtService;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, JwtService jwtService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.jwtService = jwtService;
    }

    public Page<Comment> getCommentsByPostId(ObjectId postId, int page, int pageSize) {
        Post post = postRepository.findById(postId).orElse(null); // TODO: get comments ONLY under post by post id
        if (post == null) return null;

        return commentRepository.findByIdIn(post.getComments(), PageRequest.of(page, pageSize));
    }


    public Comment saveComment(String token, ObjectId postId, Comment comment) {
        Comment savedComment = null;
        comment.setPostedOn(new Date());
        comment.setUsername(jwtService.extractUsername(token));
        Post post = null;
        try {
            post = postRepository.findById(postId).orElseThrow();
            savedComment = commentRepository.save(comment);
            post.getComments().addLast(savedComment.getId());
            postRepository.save(post);

        } catch (Exception e) {
            return null;
        }
        return savedComment;
    }

    public boolean deleteComment(String token, ObjectId postId, ObjectId commentId) {
        Comment comment = null;
        Post post = null;
        try {
            comment = commentRepository.findById(commentId).orElseThrow();
            if (!comment.getUsername().equals(jwtService.extractUsername(token))) return false; // comment is not mine

            post = postRepository.findById(postId).orElseThrow();
            commentRepository.deleteById(commentId);
            post.getComments().remove(commentId);
            postRepository.save(post);

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
