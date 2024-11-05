package com.screenvault.screenvaultAPI.post;

import com.mongodb.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/getLandingPagePosts")
    public ResponseEntity<Page<Post>> getLandingPagePosts(
            @RequestBody GetPostsRequestBody requestBody,
            @Nullable @RequestHeader("Authorization") String requestAuthorizationHeader
    ) {
        Page<Post> posts = postService.getLandingPagePostsPage(requestBody.page(), requestBody.pageSize());

        if (posts == null) return ResponseEntity.notFound().build();

        if (requestAuthorizationHeader != null) {
            postService.addUserRatingToPosts(requestAuthorizationHeader, posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/getPostsByTitles")
    public ResponseEntity<Page<Post>> getPostsByTitles(
            @RequestBody GetPostsRequestBody requestBody,
            @Nullable @RequestHeader("Authorization") String requestAuthorizationHeader
    ) {
        Page<Post> posts = postService.getLandingPagePostsPage(requestBody.page(), requestBody.pageSize());

        if (posts == null) return ResponseEntity.notFound().build();

        if (requestAuthorizationHeader != null) {
            postService.addUserRatingToPosts(requestAuthorizationHeader, posts);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/getPostsByTags")
    public ResponseEntity<Page<Post>> getPostsByTags(
            @RequestBody GetPostsRequestBody requestBody,
            @Nullable @RequestHeader("Authorization") String requestAuthorizationHeader
    ) {
        return null;
    }

    @PostMapping("/uploadPost")
    public ResponseEntity<Page<Post>> uploadPost(
            @RequestBody PostPostRequestBody requestBody,
            @Nullable @RequestHeader("Authorization") String requestAuthorizationHeader
    ) {
        return null;
    }

}
