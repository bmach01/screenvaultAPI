package com.screenvault.screenvaultAPI.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document("collection")
public class Collection {
    @Id
    private UUID id = UUID.randomUUID();
    private String ownerUsername;
    private String name;

    public Collection() {
    }

    public Collection(UUID id, String ownerUsername, String name, List<UUID> posts) {
        this.id = id;
        this.ownerUsername = ownerUsername;
        this.name = name;
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
}
