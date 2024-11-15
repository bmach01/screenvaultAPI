package com.screenvault.screenvaultAPI.collection;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.PermissionDeniedDataAccessException;
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
        return ResponseEntity.ok(collectionService.getMyCollections(token));
    }

    @PutMapping("/addPostToMyCollection")
    public ResponseEntity<CollectionResponseBody> addPostToMyCollection(
            @RequestBody AddPostToCollectionRequestBody requestBody,
            @CookieValue("TOKEN") String token
    ) {
        Collection collection = null;
        try {
            collection = collectionService.addPostToMyCollection(token, requestBody.postId(), requestBody.collectionId());
        }
        catch (PermissionDeniedDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
        catch (BadRequestException e) {
            return ResponseEntity.badRequest()
                    .body(new CollectionResponseBody(e.getMessage(), false, null));
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError()
                    .body(new CollectionResponseBody(e.getMessage(), false, null));
        }

        return ResponseEntity.ok(new CollectionResponseBody(
                "Successfully added post to the collection.",
                true,
                collection
        ));
    }

}
