package com.screenvault.screenvaultAPI.post;

import com.screenvault.screenvaultAPI.comment.Comment;
import com.screenvault.screenvaultAPI.rating.Rating;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document("post")
public class Post {
    @Id
    private UUID id;
    private String title;
    private String imageUrl;
    private String posterUsername;
    private int score;
    private int commentCount;
    private int viewCount;
    private Set<String> tags;
    private Rating.Score myScore;
    private List<Comment> comments; // present only in Post details

    public Post(
        UUID id,
        String title,
        String image,
        String posterUsername,
        int score,
        int commentCount,
        int viewCount,
        Set<String> tags,
        Rating.Score myScore,
        List<Comment> comments
    ) {
        this.id = id;
        this.title = title;
        this.imageUrl = image;
        this.posterUsername = posterUsername;
        this.score = score;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.tags = tags;
        this.myScore = myScore;
        this.comments = comments;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
