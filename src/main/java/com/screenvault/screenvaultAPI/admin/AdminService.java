package com.screenvault.screenvaultAPI.admin;

import com.screenvault.screenvaultAPI.comment.Comment;
import com.screenvault.screenvaultAPI.comment.CommentRepository;
import com.screenvault.screenvaultAPI.post.Post;
import com.screenvault.screenvaultAPI.post.PostRepository;
import com.screenvault.screenvaultAPI.report.ReportRepository;
import com.screenvault.screenvaultAPI.user.User;
import com.screenvault.screenvaultAPI.user.UserRepository;
import com.screenvault.screenvaultAPI.user.UserStatus;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;

    public AdminService(
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            ReportRepository reportRepository
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
    }

    public void banUser(String username) throws InternalError, IllegalArgumentException, NoSuchElementException {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            user.setStatus(UserStatus.BANNED);
            userRepository.save(user);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void deletePost(UUID postId) throws IllegalArgumentException, NoSuchElementException {
        Post post = postRepository.findById(postId).orElseThrow();
        commentRepository.deleteByIdIn(post.getComments());
        postRepository.deleteById(postId);
    }

    public void deleteComment(UUID postId, UUID commentId) throws IllegalArgumentException, InternalError {
        try {
            Post post = postRepository.findById(postId).orElse(null);
            if (post != null) {
                post.getComments().remove(postId);
                postRepository.save(post);
            }

            commentRepository.deleteById(postId);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public Page<Post> getPageOfReportedPosts(int page, int pageSize) {
        return postRepository.findByReportsGreaterThanZero(
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "reports"))
        ).orElse(Page.empty());
    }

    public Page<Comment> getPageOfReportedComments(int page, int pageSize) {
        return commentRepository.findByReportsGreaterThanZero(
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "reports"))
        ).orElse(Page.empty());
    }

    public Post verifyPost(UUID postId)
            throws InternalError, IllegalArgumentException, NoSuchElementException
    {
        Post post = null;
        try {
            reportRepository.deleteByReportKeyReportedObjectId(postId);

            post = postRepository.findById(postId).orElseThrow();
            post.setReportCount(0);
            post.setVerified(true);
            postRepository.save(post);
        }
        catch (NoSuchElementException ignore) {}
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return post;
    }

    public Comment verifyComment(UUID commentId)
            throws InternalError, IllegalArgumentException, NoSuchElementException
    {
        Comment comment = null;
        try {
            reportRepository.deleteByReportKeyReportedObjectId(commentId);

            comment = commentRepository.findById(commentId).orElseThrow();
            comment.setReportCount(0);
            comment.setVerified(true);
            commentRepository.save(comment);
        }
        catch (NoSuchElementException ignore) {}
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return comment;
    }
}
