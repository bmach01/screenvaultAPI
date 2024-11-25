package com.screenvault.screenvaultAPI.collection;

import java.util.UUID;

public record DeleteCollectionRequestBody(
        UUID collectionId
) {
}
