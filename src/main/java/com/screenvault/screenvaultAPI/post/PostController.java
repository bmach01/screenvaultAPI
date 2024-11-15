package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.rating.RatingService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/uploadPost")
    public ResponseEntity<UploadPostResponseBody> uploadPost(
            @RequestBody PostPostRequestBody requestBody,
            // JwtType.ACCESS_TOKEN.name()
            @CookieValue("ACCESS_TOKEN") String token
    ) {
        Post savedPost = null;
        try {
            savedPost = postService.uploadPost(requestBody.post(), requestBody.isPublic());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new UploadPostResponseBody(e.getMessage(), false, null));
        } catch (InternalError e) {
            return ResponseEntity.internalServerError()
                    .body(new UploadPostResponseBody(e.getMessage(), false, null));
        }

        return ResponseEntity.ok(
                new UploadPostResponseBody("Successfully uploaded new post", true, savedPost)
        );
    }

}
