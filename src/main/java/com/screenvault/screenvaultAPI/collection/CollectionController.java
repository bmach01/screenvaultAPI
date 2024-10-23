package com.screenvault.screenvaultAPI.collection;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody AddPostToCollectionRequestBody requestBody
    ) {
        return null;
    }

}
