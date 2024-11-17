package com.screenvault.screenvaultAPI.collection;


import java.util.UUID;

public record AddPostToCollectionRequestBody(
        UUID postId,
        UUID collectionId
) {
}
