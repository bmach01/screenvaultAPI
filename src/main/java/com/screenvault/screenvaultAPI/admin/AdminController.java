package com.screenvault.screenvaultAPI.admin;

import com.screenvault.screenvaultAPI.comment.Comment;
import com.screenvault.screenvaultAPI.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PatchMapping("/banUser")
    public ResponseEntity<DeleteResponseBody> banUser(
            @RequestBody BanUserRequestBody requestBody
    ) {
        try {
            adminService.banUser(requestBody.username());
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new DeleteResponseBody(e.getMessage(), false)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new DeleteResponseBody(e.getMessage(), false)
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new DeleteResponseBody("Successfully banned user.", true));
    }

    @PatchMapping("/unbanUser")
    public ResponseEntity<DeleteResponseBody> unbanUser(
            @RequestBody BanUserRequestBody requestBody
    ) {
        try {
            adminService.unbanUser(requestBody.username());
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new DeleteResponseBody(e.getMessage(), false)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new DeleteResponseBody(e.getMessage(), false)
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new DeleteResponseBody("Successfully unbanned user.", true));
    }

    @DeleteMapping("/deletePost")
    public ResponseEntity<DeleteResponseBody> deletePost(
            @RequestBody ManageObjectRequestBody requestBody
    ) {
        try {
            adminService.deletePost(requestBody.postId());
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new DeleteResponseBody(e.getMessage(), false)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new DeleteResponseBody(e.getMessage(), false)
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new DeleteResponseBody("Successfully deleted post.", true));
    }

    @DeleteMapping("/deleteComment")
    public ResponseEntity<DeleteResponseBody> deleteComment(
            @RequestBody ManageObjectRequestBody requestBody
    ) {
        try {
            adminService.deleteComment(requestBody.commentId());
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new DeleteResponseBody(e.getMessage(), false)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new DeleteResponseBody(e.getMessage(), false)
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new DeleteResponseBody("Successfully deleted comment.", true));
    }

    @GetMapping("/getReportedPosts")
    public ResponseEntity<ObjectResponseBody> getReportedPosts(
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        Page<Post> posts = null;
        try {
            posts = adminService.getPageOfReportedPosts(page, pageSize);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ObjectResponseBody(e.getMessage(), false, null)
            );
        }
        return ResponseEntity.ok(
                new ObjectResponseBody("Successfully fetched reported posts.", true, posts)
        );
    }

    @GetMapping("/getReportedComments")
    public ResponseEntity<ObjectResponseBody> getReportedComments(
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        Page<Comment> comments = null;

        try {
            comments = adminService.getPageOfReportedComments(page, pageSize);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ObjectResponseBody(e.getMessage(), false, null)
            );
        }
        return ResponseEntity.ok(
                new ObjectResponseBody("Successfully fetched reported comments.", true, comments)
        );
    }

    @PatchMapping("/verifyPost")
    public ResponseEntity<PatchPostResponseBody> verifyPost(
            @RequestBody ManageObjectRequestBody requestBody
    ) {
        Post savedPost = null;
        try {
            savedPost = adminService.verifyPost(requestBody.postId());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new PatchPostResponseBody(e.getMessage(), false, null)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new PatchPostResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new PatchPostResponseBody("Successfully verified post.", true, savedPost)
        );
    }

    @PatchMapping("/verifyComment")
    public ResponseEntity<PatchCommentResponseBody> verifyComment(
            @RequestBody ManageObjectRequestBody requestBody
    ) {
        Comment savedComment = null;
        try {
            savedComment = adminService.verifyComment(requestBody.commentId());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new PatchCommentResponseBody(e.getMessage(), false, null)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new PatchCommentResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new PatchCommentResponseBody("Successfully verified comment.", true, savedComment)
        );
    }

}
