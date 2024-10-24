package com.screenvault.screenvaultAPI.collection;

import com.mongodb.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collection")
public class CollectionController {

    @GetMapping("/getCollectionsByUserId")
    public ResponseEntity<Page<Collection>> getCollectionsByUserId(
            @RequestBody GetCollectionsRequestBody requestBody
    ) {
        return null;
    }

    @GetMapping("/addPostToCollection")
    public ResponseEntity<Object> addPostToCollection(
            @RequestBody AddPostToCollectionRequestBody requestBody,
            @Nullable @RequestHeader("Authorization") String requestAuthorizationHeader
    ) {
        return null;
    }

}
