package com.screenvault.screenvaultAPI.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("rating")
public class Rating {

    @Id
    private RatingKey id;
    private Score rated;
    public Rating() {
    }

    public Rating(RatingKey id, Score rated) {
        this.id = id;
        this.rated = rated;
    }

    public RatingKey getId() {
        return id;
    }

    public void setId(RatingKey id) {
        this.id = id;
    }

    public Score getRated() {
        return rated;
    }

    public void setRated(Score rated) {
        this.rated = rated;
    }

    public enum Score {
        @JsonProperty("LIKE")
        LIKE(1),

        @JsonProperty("DISLIKE")
        DISLIKE(-1);

        public final int value;

        Score(int value) {
            this.value = value;
        }
    }
}
