package com.screenvault.screenvaultAPI.report;

import com.screenvault.screenvaultAPI.comment.Comment;
import com.screenvault.screenvaultAPI.comment.CommentRepository;
import com.screenvault.screenvaultAPI.post.Post;
import com.screenvault.screenvaultAPI.post.PostRepository;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.NoSuchElementException;
import java.util.UUID;

public class ReportService {

    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;

    public ReportService(
            PostRepository postRepository,
            ReportRepository reportRepository,
            CommentRepository commentRepository
    ) {
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
        this.commentRepository = commentRepository;
    }

    public Report reportPost(String username, UUID postId)
            throws NoSuchElementException, IllegalArgumentException, InternalError
    {
        ReportKey key = new ReportKey(username, postId);
        Report report = reportRepository.findById(key).orElse(null);

        if (report != null) return report;

        try {
            Post post = postRepository.findById(postId).orElseThrow();
            if (post.isVerified()) throw new IllegalArgumentException("Post has been already verified.");

            report = new Report(key);
            post.setReportCount(post.getReportCount() + 1); // TODO: move to async
            postRepository.save(post);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return report;
    }

    public Report reportComment(String username, UUID commentId)
            throws NoSuchElementException, IllegalArgumentException, InternalError
    {
        ReportKey key = new ReportKey(username, commentId);
        Report report = reportRepository.findById(key).orElse(null);

        if (report != null) return report;

        try {
            Comment comment = commentRepository.findById(commentId).orElseThrow();
            if (comment.isVerified()) throw new IllegalArgumentException("Comment has been already verified.");

            report = new Report(key);
            comment.setReportCount(comment.getReportCount() + 1); // TODO: move to async
            commentRepository.save(comment);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return report;
    }
}
