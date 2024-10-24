package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document("comment")
public class Comment {
    @Id
    private UUID id;
    private UUID userId;
    private String text;
    private Date postedOn;

    public Comment(UUID id, UUID userId, String text, Date postedOn) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.postedOn = postedOn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Date postedOn) {
        this.postedOn = postedOn;
    }
}
