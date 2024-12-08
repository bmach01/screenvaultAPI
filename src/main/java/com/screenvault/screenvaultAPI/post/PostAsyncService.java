package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.comment.CommentRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostAsyncService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public PostAsyncService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Async
    public void incrementViewCountAndSave(UUID postId) {
        try {
            postRepository.incrementViewCount(postId);
        }
        catch (Exception ignore) {}
    }

    @Async
    public void incrementReportCountAndSave(UUID postId) {
        try {
            postRepository.incrementReportCount(postId);
        }
        catch (Exception ignore) {}
    }

    @Async
    public void incrementCommentCountAndSave(UUID postId) {
        try {
            postRepository.incrementCommentCount(postId);
        }
        catch (Exception ignore) {}
    }

    @Async
    public void decrementCommentCountAndSave(UUID postId) {
        try {
            postRepository.decrementCommentCount(postId);
        }
        catch (Exception ignore) {}
    }

    @Async
    public void removeCollectionFromPosts(UUID collectionId) {
        try {
            postRepository.removeCollectionFromPosts(collectionId);
        }
        catch (Exception ignore) {}
    }

    @Async
    public void markDeletedCommentsForPost(UUID postId) {
        try {
            commentRepository.markDeleteByPostId(postId);
        }
        catch (Exception ignore){}
    }
}
