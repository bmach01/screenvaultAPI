package com.screenvault.screenvaultAPI.collection;

import org.bson.types.ObjectId;


public record AddPostToCollectionRequestBody(
        ObjectId postId,
        ObjectId collectionId
) {
}
