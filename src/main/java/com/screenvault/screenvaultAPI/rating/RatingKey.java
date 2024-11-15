package com.screenvault.screenvaultAPI.rating;

import org.bson.types.ObjectId;

public class RatingKey {
    private ObjectId postId;
    private String username;

    public RatingKey() {
    }

    public RatingKey(ObjectId postId, String username) {
        this.postId = postId;
        this.username = username;
    }

    public ObjectId getPostId() {
        return postId;
    }

    public void setPostId(ObjectId postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}