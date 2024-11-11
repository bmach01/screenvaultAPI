package com.screenvault.screenvaultAPI.collection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collection")
public class CollectionController {
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/getMyCollections")
    public ResponseEntity<List<Collection>> getCollectionsByUserId(
            // JwtType.TOKEN.name()
            @CookieValue("TOKEN") String token
    ) {
        List<Collection> collections = collectionService.getMyCollections(token);
        if (collections == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(collections);

    }

    @PutMapping("/addPostToMyCollection")
    public ResponseEntity<String> addPostToMyCollection(
            @RequestBody AddPostToCollectionRequestBody requestBody,
            @CookieValue("TOKEN") String token
    ) {
        if (collectionService.addPostToMyCollection(token, requestBody.postId(), requestBody.collectionId())) {
            return ResponseEntity.ok("Successfully added post to the collection.");
        }
        return ResponseEntity.badRequest().build();
    }

}
