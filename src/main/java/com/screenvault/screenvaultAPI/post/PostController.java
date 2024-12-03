package com.screenvault.screenvaultAPI.post;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.screenvault.screenvaultAPI.rating.RatingService;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final RatingService ratingService;
    private final ObjectMapper objectMapper;

    public PostController(PostService postService, RatingService ratingService, ObjectMapper objectMapper) {
        this.postService = postService;
        this.ratingService = ratingService;
        this.objectMapper = objectMapper;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @GetMapping("/noAuth/getLandingPagePosts")
    public ResponseEntity<Page<Post>> getLandingPagePosts(
            @RequestBody GetPostsRequestBody requestBody,
            Principal principal
    ) {
        Page<Post> posts = postService.getLandingPagePostsPage(requestBody.page(), requestBody.pageSize());

        if (principal != null) {
            ratingService.addUserRatingToPosts(principal.getName(), posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/noAuth/getPostsByTitles")
    public ResponseEntity<Page<Post>> getPostsByTitles(
            @RequestBody GetPostsRequestBody requestBody,
            Principal principal
    ) {
        Page<Post> posts = postService.getPostsByTitle(requestBody.title(), requestBody.page(), requestBody.pageSize());

        if (principal != null) {
            ratingService.addUserRatingToPosts(principal.getName(), posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/noAuth/getPostsByTags")
    public ResponseEntity<Page<Post>> getPostsByTags(
            @RequestBody GetPostsRequestBody requestBody,
            Principal principal
    ) {
        Page<Post> posts = postService.getPostsByTags(requestBody.tags(), requestBody.page(), requestBody.pageSize());

        if (principal != null) {
            ratingService.addUserRatingToPosts(principal.getName(), posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/noAuth/getPostById")
    public ResponseEntity<Post> getPostById(
            @RequestBody GetPostsRequestBody requestBody,
            Principal principal
    ) {
        Post post = null;
        try {
            post = postService.getPostById(requestBody.postId());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        if (principal != null) {
            ratingService.addUserRatingToPosts(principal.getName(), post);
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping("/noAuth/uploadPost")
    public ResponseEntity<PostResponseBody> uploadPost(
            @RequestParam String postRequest,
            @RequestParam MultipartFile image,
            Principal principal
    ) {
        Post post = null;
        try {
            post = objectMapper.readValue(postRequest, Post.class);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new PostResponseBody("Failed to deserialize the request.", false, null));
        }

        Post savedPost = null;
        String username = principal == null ? "Anonymous" : principal.getName();
        try {
            savedPost = postService.uploadPost(username, post, image);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new PostResponseBody(e.getMessage(), false, null));
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError()
                    .body(new PostResponseBody(e.getMessage(), false, null));
        }

        return ResponseEntity.ok(
                new PostResponseBody("Successfully uploaded new post", true, savedPost)
        );
    }

    @DeleteMapping("/deletePost")
    public ResponseEntity<PostResponseBody> deletePost(
            @RequestBody DeletePostRequestBody requestBody,
            Principal principal
    ) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new PostResponseBody("Sign in to manage posts.", false, null)
            );

        try {
            postService.deletePost(principal.getName(), requestBody.postId());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }
        catch (PermissionDeniedDataAccessException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new PostResponseBody("Successfully deleted the comment", true, null)
        );
    }

    @PatchMapping("/updatePostVisibility")
    public ResponseEntity<PostResponseBody> changePostVisibility(
            @RequestBody UpdatePostVisibilityRequestBody requestBody,
            Principal principal
    ) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new PostResponseBody("Sign in to manage posts.", false, null)
            );

        Post updatedPost = null;
        try {
            updatedPost = postService.changePostVisibility(principal.getName(), requestBody.postId(), requestBody.toPublic());

        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }
        catch (PermissionDeniedDataAccessException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new PostResponseBody("Successfully updated post visibility.", true, updatedPost)
        );
    }
}
