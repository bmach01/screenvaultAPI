package com.screenvault.screenvaultAPI.collection;

public record CollectionResponseBody(
        String message,
        boolean success,
        Collection collection
) {
}
