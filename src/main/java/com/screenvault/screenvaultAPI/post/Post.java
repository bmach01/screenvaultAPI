package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.rating.Rating;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Document("post")
public class Post {
    @Id
    private ObjectId id;
    private String title;
    private String imageUrl;
    private String posterUsername;
    private int score = 0;
    private int commentCount = 0;
    private int viewCount = 0;
    private Date postedOn;
    private Set<String> tags;
    private Rating.Score myScore;
    private List<ObjectId> comments; // present only in Post details
    private boolean isPublic; // for faster search

    public Post(ObjectId id, String title, String imageUrl, String posterUsername, int score, int commentCount, int viewCount, Date postedOn, Set<String> tags, Rating.Score myScore, List<ObjectId> comments, boolean isPublic) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.posterUsername = posterUsername;
        this.score = score;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.postedOn = postedOn;
        this.tags = tags;
        this.myScore = myScore;
        this.comments = comments;
        this.isPublic = isPublic;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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

    public List<ObjectId> getComments() {
        return comments;
    }

    public void setComments(List<ObjectId> comments) {
        this.comments = comments;
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
}
