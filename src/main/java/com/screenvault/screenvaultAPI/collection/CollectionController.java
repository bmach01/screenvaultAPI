package com.screenvault.screenvaultAPI.collection;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/collection")
public class CollectionController {
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/getMyCollections")
    public ResponseEntity<GetCollectionsResponseBody> getCollectionsByUserId(
            Principal principal
    ) {
        return ResponseEntity.ok(
                new GetCollectionsResponseBody(
                        "Successfully fetched my collections.",
                        true,
                        collectionService.getMyCollections(principal.getName())
                )
        );
    }

    @PatchMapping("/renameCollection")
    public ResponseEntity<CollectionResponseBody> getCollectionById(
            @RequestBody RenameCollectionRequestBody requestBody,
            Principal principal
    ) {
        Collection collection = null;

        try {
            collection = collectionService.renameCollection(
                    principal.getName(),
                    requestBody.collectionId(),
                    requestBody.newName()
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new CollectionResponseBody("Successfully renamed the collection.", true, collection)
        );
    }

    @PatchMapping("/addPostToMyCollection")
    public ResponseEntity<CollectionResponseBody> addPostToMyCollection(
            @RequestBody AddPostToCollectionRequestBody requestBody,
            Principal principal
    ) {
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

    @PatchMapping("/removePostFromMyCollection")
    public ResponseEntity<CollectionResponseBody> removePostFromMyCollection(
            @RequestBody AddPostToCollectionRequestBody requestBody,
            Principal principal
    ) {
        Collection collection = null;

        try {
            collection = collectionService.removePostFromMyCollection(principal.getName(), requestBody.postId(), requestBody.collectionId());
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
                "Successfully removed post from the collection.",
                true,
                collection
        ));
    }

    @PostMapping("/postCollection")
    public ResponseEntity<CollectionResponseBody> createNewCollection(
            @RequestBody PostCollectionRequestBody requestBody,
            Principal principal
    ) {
        Collection savedCollection = null;

        try {
            savedCollection = collectionService.uploadCollection(principal.getName(), requestBody.collection());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new CollectionResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new CollectionResponseBody("Successfully created new collection", true, savedCollection)
        );
    }

    @DeleteMapping("/deleteCollection")
    public ResponseEntity<CollectionResponseBody> deleteCollection(
            @RequestBody DeleteCollectionRequestBody requestBody,
            Principal principal
    ) {
        try {
            collectionService.deleteCollection(principal.getName(), requestBody.collectionId());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new CollectionResponseBody(e.getMessage(), false, null)
            );
        }
        catch (PermissionDeniedDataAccessException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new CollectionResponseBody(e.getMessage(), false, null)
            );
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new CollectionResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new CollectionResponseBody("Successfully deleted the collection.", true, null)
        );
    }

}
