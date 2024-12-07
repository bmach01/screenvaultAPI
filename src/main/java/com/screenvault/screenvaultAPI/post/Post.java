package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.rating.Rating;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Document("post")
public class Post {
    @Id
    private UUID id = UUID.randomUUID();
    private String title;
    private String posterUsername;
    private String imageUrl; // not stored in the db
    private Set<UUID> collectionIds = Collections.emptySet();
    private int score = 0;
    private int commentCount = 0;
    private int viewCount = 0;
    private Date postedOn = new Date();
    private Set<String> tags = Collections.emptySet(); // present only in Post details
    private Rating.Score myScore;
    private boolean isPublic;
    private boolean isVerified = false;
    private Integer reportCount = 0; // present only in admin cockpit
    private boolean isDeleted = false;

    public Post() {
    }

    public Post(
            UUID id,
            String title,
            String imageUrl,
            String posterUsername,
            Set<UUID> collectionIds,
            int score,
            int commentCount,
            int viewCount,
            Date postedOn,
            Set<String> tags,
            Rating.Score myScore,
            boolean isPublic,
            boolean isVerified,
            Integer reportCount,
            boolean isDeleted
    ) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.posterUsername = posterUsername;
        this.collectionIds = collectionIds;
        this.score = score;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.postedOn = postedOn;
        this.tags = tags;
        this.myScore = myScore;
        this.isPublic = isPublic;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPosterUsername() {
        return posterUsername;
    }

    public void setPosterUsername(String posterUsername) {
        this.posterUsername = posterUsername;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Rating.Score getMyScore() {
        return myScore;
    }

    public void setMyScore(Rating.Score myScore) {
        this.myScore = myScore;
    }

    public Date getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Date postedOn) {
        this.postedOn = postedOn;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public Set<UUID> getCollectionIds() {
        return collectionIds;
    }

    public void setCollectionIds(Set<UUID> collectionIds) {
        this.collectionIds = collectionIds;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
