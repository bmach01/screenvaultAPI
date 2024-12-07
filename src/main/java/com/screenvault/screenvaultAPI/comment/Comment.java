package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document("comment")
public class Comment {
    @Id
    private UUID id = UUID.randomUUID();
    private UUID postId;
    private String username;
    private String text;
    private Date postedOn = new Date();
    private boolean isVerified = false;
    private int reportCount = 0;
    private boolean isDeleted = false;

    public Comment() {
    }

    public Comment(
            UUID id,
            UUID postId,
            String username,
            String text,
            Date postedOn,
            boolean isVerified,
            int reportCount,
            boolean isDeleted
    ) {
        this.id = id;
        this.postId = postId;
        this.username = username;
        this.text = text;
        this.postedOn = postedOn;
        this.isVerified = isVerified;
        this.reportCount = reportCount;
        this.isDeleted = isDeleted;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
