package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/getCommentsUnderPost")
    public ResponseEntity<Page<Comment>> getCommentsUnderPost(
            @RequestBody GetCommentsRequestBody requestBody
    ) {
        Page<Comment> comments = commentService.getCommentsByPostId(
                requestBody.postId(),
                requestBody.page(),
                requestBody.pageSize()
        );
        return comments == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(comments);
    }

    @PostMapping("/postComment")
    public ResponseEntity<Comment> postCommentUnderPost(
            @RequestBody PostCommentRequestBody requestBody,
            // JwtType.TOKEN.name()
            @CookieValue("TOKEN") String token
    ) {
        Comment savedComment = commentService.saveComment(token, requestBody.postId(), requestBody.comment());
        if (savedComment != null) {
            return ResponseEntity.ok(savedComment);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/deleteComment")
    public ResponseEntity<Void> deleteComment(
            @RequestBody DeleteCommentRequestBody requestBody,
            // JwtType.TOKEN.name()
            @CookieValue("TOKEN") String token
    ) {
        return commentService.deleteComment(token, requestBody.postId(), requestBody.commentId()) ?
                ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

}
