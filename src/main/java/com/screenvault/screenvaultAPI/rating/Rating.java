package com.screenvault.screenvaultAPI.rating;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("rating")
public class Rating {
    public enum Score { LIKE, DISLIKE }
    @Id
    private UUID id;
    private String posterUsername;
    private UUID postId;
    private Score rated;

    public Rating(UUID id, String posterUsername, UUID postId, Score rated) {
        this.id = id;
        this.posterUsername = posterUsername;
        this.postId = postId;
        this.rated = rated;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPosterUsername() {
        return posterUsername;
    }

    public void setPosterUsername(String posterUsername) {
        this.posterUsername = posterUsername;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public Score getRated() {
        return rated;
    }

    public void setRated(Score rated) {
        this.rated = rated;
    }
}
