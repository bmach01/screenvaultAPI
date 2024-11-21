package com.screenvault.screenvaultAPI.shortKey;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document("shortKey")
public class ShortKey {
    @Id
    private UUID postId;
    private String key;
    private Date createdOn;

    public ShortKey(UUID postId, String key, Date createdOn) {
        this.postId = postId;
        this.key = key;
        this.createdOn = createdOn;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
