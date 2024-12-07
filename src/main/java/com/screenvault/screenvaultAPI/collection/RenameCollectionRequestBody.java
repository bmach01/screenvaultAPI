package com.screenvault.screenvaultAPI.collection;

import java.util.UUID;

public record RenameCollectionRequestBody(
        UUID collectionId,
        String newName
) {
}
