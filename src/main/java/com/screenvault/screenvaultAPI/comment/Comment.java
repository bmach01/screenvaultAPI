package com.screenvault.screenvaultAPI.comment;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("comment")
public class Comment {
    @Id
    private ObjectId id;
    private String username;
    private String text;
    private Date postedOn;

    public Comment() {
    }

    public Comment(ObjectId id, String username, String text, Date postedOn) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.postedOn = postedOn;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
