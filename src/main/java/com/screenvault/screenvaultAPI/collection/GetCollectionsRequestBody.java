package com.screenvault.screenvaultAPI.collection;

import java.util.UUID;

public class GetCollectionsRequestBody {
    private int page;
    private int pageSize;
    private UUID collectionId;
    private UUID ownerId;
}
