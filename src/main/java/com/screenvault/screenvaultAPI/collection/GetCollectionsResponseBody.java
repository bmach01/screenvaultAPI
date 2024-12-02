package com.screenvault.screenvaultAPI.collection;

import java.util.List;

public record GetCollectionsResponseBody(
        String message,
        boolean success,
        List<Collection> collectionList
) {
}
