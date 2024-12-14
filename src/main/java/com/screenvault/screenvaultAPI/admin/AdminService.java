package com.screenvault.screenvaultAPI.admin;

import com.screenvault.screenvaultAPI.comment.Comment;
import com.screenvault.screenvaultAPI.comment.CommentRepository;
import com.screenvault.screenvaultAPI.image.ImageService;
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
    private final ImageService imageService;

    public AdminService(
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            ReportRepository reportRepository,
            ImageService imageService
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
        this.imageService = imageService;
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

    public void unbanUser(String username) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            user.setStatus(UserStatus.INACTIVE);
            userRepository.save(user);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void deletePost(UUID postId) throws IllegalArgumentException, NoSuchElementException {
        try {
            Post post = postRepository.findById(postId).orElseThrow();
            post.setDeleted(true);
            postRepository.save(post);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

    }

    public void deleteComment(UUID commentId) throws IllegalArgumentException, InternalError {
        try {
            Comment comment = commentRepository.findById(commentId).orElseThrow();
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public Page<Post> getPageOfReportedPosts(int page, int pageSize) throws IllegalArgumentException {
        Page<Post> posts = postRepository.findByReportsGreaterThanZero(
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "reportCount"))
        );
        posts.getContent().forEach((it) -> {
            it.setImageUrl(imageService.getImageUrl(it.getId().toString(), it.isPublic()));
        });

        return posts;
    }

    public Page<Comment> getPageOfReportedComments(int page, int pageSize) throws IllegalArgumentException {
        return commentRepository.findReported(PageRequest.of(page, pageSize));
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
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return comment;
    }
}
