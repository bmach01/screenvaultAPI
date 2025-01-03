package com.screenvault.screenvaultAPI.comment;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/noAuth/getCommentsUnderPost")
    public ResponseEntity<GetCommentsResponseBody> getCommentsUnderPost(
            @RequestParam UUID postId,
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        Page<Comment> comments = null;

        try {
            comments = commentService.getCommentsByPostId(postId, page, pageSize);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new GetCommentsResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new GetCommentsResponseBody(
                        "Successfully fetched comments.",
                        true,
                        CommentUserView.mapPage(comments)
                )
        );
    }

    @PostMapping("/postComment")
    public ResponseEntity<CommentResponseBody> postCommentUnderPost(
            @RequestHeader("X-CSRF-TOKEN") String csrfToken,
            @RequestBody PostCommentRequestBody requestBody,
            Principal principal
    ) {
        Comment savedComment = null;

        try {
            savedComment = commentService.uploadComment(principal.getName(), requestBody.postId(), requestBody.comment());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new CommentResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new CommentResponseBody("Successfully uploaded comment", true, new CommentUserView(savedComment))
        );
    }

    @DeleteMapping("/deleteComment")
    public ResponseEntity<CommentResponseBody> deleteComment(
            @RequestBody DeleteCommentRequestBody requestBody,
            Principal principal
    ) {
        try {
            commentService.userMarkCommentDeleted(principal.getName(), requestBody.commentId());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new CommentResponseBody(e.getMessage(), false, null)
            );
        }
        catch (PermissionDeniedDataAccessException | NoSuchElementException e) {
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
