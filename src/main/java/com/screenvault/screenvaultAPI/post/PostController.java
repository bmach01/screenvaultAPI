package com.screenvault.screenvaultAPI.post;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.rating.RatingService;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final RatingService ratingService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public PostController(
            PostService postService,
            RatingService ratingService,
            JwtService jwtService,
            ObjectMapper objectMapper
    ) {
        this.postService = postService;
        this.ratingService = ratingService;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @GetMapping("/noAuth/getLandingPagePosts")
    public ResponseEntity<PostsResponseBody> getLandingPagePosts(
            @RequestParam int page,
            @RequestParam int pageSize,
            // JwtType.ACCESS_TOKEN.name() <-- annotation can't use variable value, also can't use Principal because it
            // works only for endpoints that require authentication
            @CookieValue(name = "ACCESS_TOKEN", defaultValue = "") String token
    ) {
        Page<Post> posts = postService.getLandingPagePostsPage(page, pageSize);

        if (!token.isBlank()) {
            ratingService.addUserRatingToPosts(jwtService.extractUsername(token), posts);
        }

        return ResponseEntity.ok(
                new PostsResponseBody("Successfully fetched posts.", true, PostUserView.mapPage(posts))
        );
    }

    @GetMapping("/noAuth/getPostsByTitles")
    public ResponseEntity<PostsResponseBody> getPostsByTitles(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam String title,
            // JwtType.ACCESS_TOKEN.name() <-- annotation can't use variable value, also can't use Principal because it
            // works only for endpoints that require authentication
            @CookieValue(name = "ACCESS_TOKEN", defaultValue = "") String token
    ) {
        Page<Post> posts = postService.getPostsByTitle(title, page, pageSize);

        if (!token.isBlank()) {
            ratingService.addUserRatingToPosts(jwtService.extractUsername(token), posts);
        }

        return ResponseEntity.ok(
                new PostsResponseBody("Successfully fetched posts.", true, PostUserView.mapPage(posts))
        );
    }

    @GetMapping("/noAuth/getPostsByTags")
    public ResponseEntity<PostsResponseBody> getPostsByTags(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam Set<String> tags,
            // JwtType.ACCESS_TOKEN.name() <-- annotation can't use variable value, also can't use Principal because it
            // works only for endpoints that require authentication
            @CookieValue(name = "ACCESS_TOKEN", defaultValue = "") String token
    ) {
        Page<Post> posts = postService.getPostsByTags(tags, page, pageSize);

        if (!token.isBlank()) {
            ratingService.addUserRatingToPosts(jwtService.extractUsername(token), posts);
        }

        return ResponseEntity.ok(
                new PostsResponseBody("Successfully fetched posts.", true, PostUserView.mapPage(posts))
        );
    }

    @GetMapping("/noAuth/getPostById")
    public ResponseEntity<PostResponseBody> getPostById(
            @RequestParam UUID postId,
            // JwtType.ACCESS_TOKEN.name() <-- annotation can't use variable value, also can't use Principal because it
            // works only for endpoints that require authentication
            @CookieValue(name = "ACCESS_TOKEN", defaultValue = "") String token
    ) {
        Post post = null;

        try {
            post = postService.getPostById(postId);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        if (!token.isBlank()) {
            ratingService.addUserRatingToPosts(jwtService.extractUsername(token), post);
        }

        return ResponseEntity.ok(new PostResponseBody("Successfully fetched post.", true, new PostUserView(post)));
    }

    @GetMapping("/getPostsByCollectionId")
    public ResponseEntity<PostsResponseBody> getPostsFromCollection(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam UUID collectionId,
            // JwtType.ACCESS_TOKEN.name() <-- annotation can't use variable value, also can't use Principal because it
            // works only for endpoints that require authentication
            @CookieValue(name = "ACCESS_TOKEN", defaultValue = "") String token
    ) {
        Page<Post> posts = null;

        try {
            posts = postService.getPostsByCollectionId(collectionId, page, pageSize);

            if (!token.isBlank()) {
                ratingService.addUserRatingToPosts(jwtService.extractUsername(token), posts);
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new PostsResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new PostsResponseBody("Successfully fetched posts.", true, PostUserView.mapPage(posts))
        );
    }

    @PostMapping("/noAuth/uploadPost")
    public ResponseEntity<PostResponseBody> uploadPost(
            @RequestParam String postRequest,
            @RequestParam MultipartFile image,
            // JwtType.ACCESS_TOKEN.name() <-- annotation can't use variable value, also can't use Principal because it
            // works only for endpoints that require authentication
            @CookieValue(name = "ACCESS_TOKEN", defaultValue = "") String token
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
        String username = token.isBlank() ? "Anonymous" : jwtService.extractUsername(token);

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
                new PostResponseBody("Successfully uploaded new post", true, new PostUserView(savedPost))
        );
    }

    @DeleteMapping("/deletePost")
    public ResponseEntity<PostResponseBody> deletePost(
            @RequestBody DeletePostRequestBody requestBody,
            Principal principal
    ) {
        try {
            postService.userMarkPostDeleted(principal.getName(), requestBody.postId());
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
        Post updatedPost = null;

        try {
            updatedPost = postService.changePostVisibility(principal.getName(), requestBody.postId(), requestBody.toPublic());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }
        catch (PermissionDeniedDataAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new PostResponseBody("Successfully updated post visibility.", true, new PostUserView(updatedPost))
        );
    }
}
