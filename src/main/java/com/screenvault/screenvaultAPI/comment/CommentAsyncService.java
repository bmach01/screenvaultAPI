package com.screenvault.screenvaultAPI.comment;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CommentAsyncService {

    private final CommentRepository commentRepository;

    public CommentAsyncService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Async
    public void incrementReportCountAndSave(Comment comment) {
        comment.setReportCount(comment.getReportCount() + 1);
        try {
            commentRepository.save(comment);
        }
        catch (Exception ignore) {}
    }
}
