package com.screenvault.screenvaultAPI.collection;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("collection")
public class Collection {
    @Id
    private ObjectId id;
    private String ownerUsername;
    private String name;
    private boolean isPrivate;
    private List<ObjectId> posts; // present only in Collection details
    private boolean isGlobal = false;

    public Collection(ObjectId id, String ownerUsername, String name, boolean isPrivate, List<ObjectId> posts) {
        this.id = id;
        this.ownerUsername = ownerUsername;
        this.name = name;
        this.isPrivate = isPrivate;
        this.posts = posts;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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
