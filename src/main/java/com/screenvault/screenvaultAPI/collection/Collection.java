package com.screenvault.screenvaultAPI.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document("collection")
public class Collection {
    @Id
    private UUID id;
    private UUID ownerId;
    private String name;
    private boolean isPrivate;
    private List<UUID> posts; // present only in Collection details

    public Collection(UUID id, UUID ownerId, String name, boolean isPrivate, List<UUID> posts) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.isPrivate = isPrivate;
        this.posts = posts;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public List<UUID> getPosts() {
        return posts;
    }

    public void setPosts(List<UUID> posts) {
        this.posts = posts;
    }
}
