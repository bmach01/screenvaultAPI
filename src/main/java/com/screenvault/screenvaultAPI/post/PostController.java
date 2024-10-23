package com.screenvault.screenvaultAPI.post;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

    @GetMapping("/getLandingPagePosts")
    public ResponseEntity<Page<Post>> getLandingPagePosts(
            @RequestBody GetPostsRequestBody requestBody
    ) {
        return null;
    }

    @GetMapping("/getPostsByTitles")
    public ResponseEntity<Page<Post>> getPostsByTitles(
            @RequestBody GetPostsRequestBody requestBody
    ) {
        return null;
    }

    @GetMapping("/getPostsByTags")
    public ResponseEntity<Page<Post>> getPostsByTags(
            @RequestBody GetPostsRequestBody requestBody
    ) {
        return null;
    }

    @PostMapping("/uploadPost")
    public ResponseEntity<Page<Post>> uploadPost(
            @RequestBody PostPostRequestBody requestBody
    ) {
        return null;
    }

}
