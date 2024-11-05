package com.screenvault.screenvaultAPI.collection;

import com.mongodb.lang.Nullable;
import com.screenvault.screenvaultAPI.post.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collection")
public class CollectionController {

    private final PostService postService;
    private final CollectionService collectionService;

    @GetMapping("/getCollectionsByUserId")
    public ResponseEntity<Page<Collection>> getCollectionsByUserId(
            @RequestBody GetCollectionsRequestBody requestBody
    ) {
        return null;
    }

    @PutMapping("/addPostToCollection")
    public ResponseEntity<Object> addPostToCollection(
            @RequestBody AddPostToCollectionRequestBody requestBody,
            @Nullable @RequestHeader("Authorization") String requestAuthorizationHeader
    ) {
        if (service.addPostToCollection(requestBody.post())) {
            return ResponseEntity.ok(responseBody);
        }
        return ResponseEntity.badRequest().build();
    }

}
