package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document("comment")
public class Comment {
    @Id
    private UUID id = UUID.randomUUID();
    private String username;
    private String text;
    private Date postedOn;
    private boolean verified = false;
    private int reportCount = 0;

    public Comment() {
    }

    public Comment(UUID id, String username, String text, Date postedOn, boolean verified, int reportCount) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.postedOn = postedOn;
        this.verified = verified;
        this.reportCount = reportCount;
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
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }
}
