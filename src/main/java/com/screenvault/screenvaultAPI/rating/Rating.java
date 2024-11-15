package com.screenvault.screenvaultAPI.rating;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("rating")
public class Rating {
    @Id
    private ObjectId id;
    private String posterUsername;
    private ObjectId postId;
    private Score rated;

    public Rating() {
    }

    public Rating(ObjectId id, String posterUsername, ObjectId postId, Score rated) {
        this.id = id;
        this.posterUsername = posterUsername;
        this.postId = postId;
        this.rated = rated;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPosterUsername() {
        return posterUsername;
    }

    public void setPosterUsername(String posterUsername) {
        this.posterUsername = posterUsername;
    }

    public ObjectId getPostId() {
        return postId;
    }

    public void setPostId(ObjectId postId) {
        this.postId = postId;
    }

    public Score getRated() {
        return rated;
    }

    public void setRated(Score rated) {
        this.rated = rated;
    }

    public enum Score {
        LIKE(1), DISLIKE(-1);

        public final int value;

        Score(int value) {
            this.value = value;
        }
    }
}
