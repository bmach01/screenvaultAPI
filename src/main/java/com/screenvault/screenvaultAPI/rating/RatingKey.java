package com.screenvault.screenvaultAPI.rating;

import java.util.UUID;

public class RatingKey {
    private UUID postId;
    private String username;

    public RatingKey() {
    }

    public RatingKey(UUID postId, String username) {
        this.postId = postId;
        this.username = username;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}