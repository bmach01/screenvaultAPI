package com.screenvault.screenvaultAPI.collection;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Document("collection")
public class Collection {
    @Id
    private UUID id = UUID.randomUUID();
    private String ownerUsername;
    private String name;
    private boolean isPrivate;
    private List<ObjectId> posts = Collections.emptyList(); // present only in Collection details
    private boolean isGlobal = false;

    public Collection() {
    }

    public Collection(UUID id, String ownerUsername, String name, boolean isPrivate, List<ObjectId> posts) {
        this.id = id;
        this.ownerUsername = ownerUsername;
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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
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

    public List<ObjectId> getPosts() {
        return posts;
    }

    public void setPosts(List<ObjectId> posts) {
        this.posts = posts;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }
}
