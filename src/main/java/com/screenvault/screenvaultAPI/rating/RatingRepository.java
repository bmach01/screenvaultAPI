package com.screenvault.screenvaultAPI.rating;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface RatingRepository extends MongoRepository<Rating, UUID> {

    List<Rating> findByPosterUsernameAndPostId(String posterUsername, UUID postId);

}
