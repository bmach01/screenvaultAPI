package com.screenvault.screenvaultAPI.comment;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/noAuth/getCommentsUnderPost")
    public ResponseEntity<Page<Comment>> getCommentsUnderPost(
            @RequestBody GetCommentsRequestBody requestBody
    ) {
        Page<Comment> comments = commentService.getCommentsByPostId(
                requestBody.postId(),
                requestBody.page(),
                requestBody.pageSize()
        );
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/postComment")
    public ResponseEntity<CommentResponseBody> postCommentUnderPost(
            @RequestBody PostCommentRequestBody requestBody,
            Principal principal
    ) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new CommentResponseBody("Sign in to manage comments.", false, null)
            );

        Comment savedComment = null;
        try {
            savedComment = commentService.uploadComment(principal.getName(), requestBody.postId(), requestBody.comment());
        }
        catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(
                    new CommentResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new CommentResponseBody("Successfully uploaded comment", true, savedComment)
        );
    }

    @DeleteMapping("/deleteComment")
    public ResponseEntity<CommentResponseBody> deleteComment(
            @RequestBody DeleteCommentRequestBody requestBody,
            Principal principal
    ) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new CommentResponseBody("Sign in to manage comments.", false, null)
            );

        try {
            commentService.deleteComment(principal.getName(), requestBody.postId(), requestBody.commentId());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new CommentResponseBody(e.getMessage(), false, null)
            );
        }
        catch (PermissionDeniedDataAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new CommentResponseBody(e.getMessage(), false, null)
            );
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new CommentResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new CommentResponseBody("Successfully deleted the comment", true, null)
        );
    }

}
