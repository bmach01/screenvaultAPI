package com.screenvault.screenvaultAPI.post;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.screenvault.screenvaultAPI.collection.CollectionService;
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
    private final CollectionService collectionService;
    private final ObjectMapper objectMapper;

    public PostController(
            PostService postService,
            RatingService ratingService,
            CollectionService collectionService,
            ObjectMapper objectMapper
    ) {
        this.postService = postService;
        this.ratingService = ratingService;
        this.collectionService = collectionService;
        this.objectMapper = objectMapper;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @GetMapping("/noAuth/getLandingPagePosts")
    public ResponseEntity<Page<Post>> getLandingPagePosts(
            @RequestParam int page,
            @RequestParam int pageSize,
            Principal principal
    ) {
        Page<Post> posts = postService.getLandingPagePostsPage(page, pageSize);

        if (principal != null) {
            ratingService.addUserRatingToPosts(principal.getName(), posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/noAuth/getPostsByTitles")
    public ResponseEntity<Page<Post>> getPostsByTitles(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam String title,
            Principal principal
    ) {
        Page<Post> posts = postService.getPostsByTitle(title, page, pageSize);

        if (principal != null) {
            ratingService.addUserRatingToPosts(principal.getName(), posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/noAuth/getPostsByTags")
    public ResponseEntity<Page<Post>> getPostsByTags(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam Set<String> tags,
            Principal principal
    ) {
        Page<Post> posts = postService.getPostsByTags(tags, page, pageSize);

        if (principal != null) {
            ratingService.addUserRatingToPosts(principal.getName(), posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/noAuth/getPostById")
    public ResponseEntity<Post> getPostById(
            @RequestParam UUID postId,
            Principal principal
    ) {
        Post post = null;
        try {
            post = postService.getPostById(postId);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        if (principal != null) {
            ratingService.addUserRatingToPosts(principal.getName(), post);
        }
        return ResponseEntity.ok(post);
    }

    @GetMapping("/getPostsByCollectionId")
    public ResponseEntity<Page<Post>> getPostsFromCollection(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam UUID collectionId,
            Principal principal
    ) {
        try {
            Page<UUID> postIds = collectionService.getPaginatedPostIds(collectionId, page, pageSize);
            Page<Post> posts = postService.getPostsByIds(postIds);

            if (principal != null) {
                ratingService.addUserRatingToPosts(principal.getName(), posts);
            }

            return ResponseEntity.ok(posts);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
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
