package com.screenvault.screenvaultAPI.collection;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/collection")
public class CollectionController {
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/getMyCollections")
    public ResponseEntity<List<Collection>> getCollectionsByUserId(
            Principal principal
    ) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(collectionService.getMyCollections(principal.getName()));
    }

    @PatchMapping("/addPostToMyCollection")
    public ResponseEntity<CollectionResponseBody> addPostToMyCollection(
            @RequestBody AddPostToCollectionRequestBody requestBody,
            Principal principal
    ) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new CollectionResponseBody("Sign in to manage collections.", false, null)
            );

        Collection collection = null;
        try {
            collection = collectionService.addPostToMyCollection(principal.getName(), requestBody.postId(), requestBody.collectionId());
        }
        catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(
                    new CollectionResponseBody(e.getMessage(), false, null)
            );
        }
        catch (PermissionDeniedDataAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new CollectionResponseBody(e.getMessage(), false, null)
            );
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new CollectionResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(new CollectionResponseBody(
                "Successfully added post to the collection.",
                true,
                collection
        ));
    }

    @PostMapping("/postCollection")
    public ResponseEntity<CollectionResponseBody> createNewCollection(
            @RequestBody PostCollectionRequestBody requestBody,
            Principal principal
    ) {
        if (principal == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new CollectionResponseBody("Sign in to manage collections.", false, null)
            );

        Collection savedCollection = null;

        try {
            savedCollection = collectionService.uploadCollection(principal.getName(), requestBody.collection());
        }
        catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(
                    new CollectionResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new CollectionResponseBody("Successfully created new collection", true, savedCollection)
        );
    }

}
