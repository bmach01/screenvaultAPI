package com.screenvault.screenvaultAPI.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.screenvault.screenvaultAPI.rating.RatingService;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final RatingService ratingService;

    public PostController(PostService postService, RatingService ratingService) {
        this.postService = postService;
        this.ratingService = ratingService;
    }

    @GetMapping("/getLandingPagePosts")
    public ResponseEntity<Page<Post>> getLandingPagePosts(
            @RequestBody GetPostsRequestBody requestBody,
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token
    ) {
        Page<Post> posts = postService.getLandingPagePostsPage(requestBody.page(), requestBody.pageSize());

        if (token != null) {
            ratingService.addUserRatingToPosts(token, posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/getPostsByTitles")
    public ResponseEntity<Page<Post>> getPostsByTitles(
            @RequestBody GetPostsRequestBody requestBody,
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token
    ) {
        Page<Post> posts = postService.getPostsByTitle(requestBody.title(), requestBody.page(), requestBody.pageSize());

        if (token != null) {
            ratingService.addUserRatingToPosts(token, posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/getPostsByTags")
    public ResponseEntity<Page<Post>> getPostsByTags(
            @RequestBody GetPostsRequestBody requestBody,
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token
    ) {
        Page<Post> posts = postService.getPostsByTags(requestBody.tags(), requestBody.page(), requestBody.pageSize());

        if (token != null) {
            ratingService.addUserRatingToPosts(token, posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/getPostById")
    public ResponseEntity<Post> getPostById(
            @RequestBody GetPostsRequestBody requestBody,
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token
    ) {
        Post post = null;
        try {
            post = postService.getPostById(requestBody.postId());
            postService.incrementViewCountAndSave(post); // This has to be done outside the method class
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        if (token != null) {
            ratingService.addUserRatingToPosts(token, post);
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping("/uploadPost")
    public ResponseEntity<PostResponseBody> uploadPost(
            @RequestParam String postRequest,
            @RequestParam MultipartFile image,
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token
    ) {
        UploadPostRequestParam postRequestDTO = null;
        try {
            postRequestDTO = new ObjectMapper().readValue(postRequest, UploadPostRequestParam.class);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new PostResponseBody("Failed to deserialize the request.", false, null));
        }

        Post savedPost = null;
        try {
            savedPost = postService.uploadPost(token, postRequestDTO.post(), postRequestDTO.isPublic(), image);
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
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token
    ) {
        try {
            postService.deletePost(token, requestBody.postId());
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
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token
    ) {
        Post updatedPost = null;
        try {
            updatedPost = postService.changePostVisiblity(token, requestBody.postId(), requestBody.toPublic());

        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new PostResponseBody(e.getMessage(), false, null)
            );
        }
        catch (PermissionDeniedDataAccessException |
               NoSuchElementException e) { // TODO: reconsider NoSuchElementException being handled this way (privacy)
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
