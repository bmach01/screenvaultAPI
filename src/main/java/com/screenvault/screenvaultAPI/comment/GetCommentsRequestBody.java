package com.screenvault.screenvaultAPI.comment;

import org.bson.types.ObjectId;

public class GetCommentsRequestBody {
    private int page;
    private int pageSize;
    private ObjectId postId;
}
