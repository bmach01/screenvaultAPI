package com.screenvault.screenvaultAPI.report;

import com.screenvault.screenvaultAPI.comment.Comment;
import com.screenvault.screenvaultAPI.comment.CommentAsyncService;
import com.screenvault.screenvaultAPI.comment.CommentRepository;
import com.screenvault.screenvaultAPI.post.Post;
import com.screenvault.screenvaultAPI.post.PostAsyncService;
import com.screenvault.screenvaultAPI.post.PostRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReportService {

    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;

    private final PostAsyncService postAsyncService;
    private final CommentAsyncService commentAsyncService;

    public ReportService(
            PostRepository postRepository,
            ReportRepository reportRepository,
            CommentRepository commentRepository,
            PostAsyncService postAsyncService,
            CommentAsyncService commentAsyncService
    ) {
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
        this.commentRepository = commentRepository;
        this.postAsyncService = postAsyncService;
        this.commentAsyncService = commentAsyncService;
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
            postAsyncService.incrementReportCountAndSave(post);
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
            commentAsyncService.incrementReportCountAndSave(comment);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return report;
    }
}
