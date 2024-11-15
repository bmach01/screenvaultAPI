package com.screenvault.screenvaultAPI.post;

import com.mongodb.lang.Nullable;
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
            // JwtType.TOKEN.name()
            @Nullable @CookieValue("TOKEN") String token
    ) {
        Page<Post> posts = postService.getLandingPagePostsPage(requestBody.page(), requestBody.pageSize());

        if (posts == null || posts.isEmpty()) return ResponseEntity.notFound().build();

        if (token != null) {
            ratingService.addUserRatingToPosts(token, posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/getPostsByTitles")
    public ResponseEntity<Page<Post>> getPostsByTitles(
            @RequestBody GetPostsRequestBody requestBody,
            // JwtType.TOKEN.name()
            @Nullable @CookieValue("TOKEN") String token
    ) {
        Page<Post> posts = postService.getPostsByTitle(requestBody.title(), requestBody.page(), requestBody.pageSize());

        if (posts == null || posts.isEmpty()) return ResponseEntity.notFound().build();

        if (token != null) {
            ratingService.addUserRatingToPosts(token, posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/getPostsByTags")
    public ResponseEntity<Page<Post>> getPostsByTags(
            @RequestBody GetPostsRequestBody requestBody,
            // JwtType.TOKEN.name()
            @Nullable @CookieValue("TOKEN") String token
    ) {
        Page<Post> posts = postService.getPostsByTags(requestBody.tags(), requestBody.page(), requestBody.pageSize());

        if (posts == null || posts.isEmpty()) return ResponseEntity.notFound().build();

        if (token != null) {
            ratingService.addUserRatingToPosts(token, posts);
        }

        return ResponseEntity.ok(posts);
    }

    @PostMapping("/uploadPost")
    public ResponseEntity<Post> uploadPost(
            @RequestBody PostPostRequestBody requestBody,
            // JwtType.TOKEN.name()
            @Nullable @CookieValue("TOKEN") String token
    ) {
        Post savedPost = postService.savePost(requestBody.post(), requestBody.isPublic());
        if (savedPost != null) {
            return ResponseEntity.ok(savedPost);
        }
        return ResponseEntity.badRequest().build();
    }

}
