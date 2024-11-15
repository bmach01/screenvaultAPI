package com.screenvault.screenvaultAPI.rating;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends MongoRepository<Rating, ObjectId> {

    List<Rating> findByPosterUsernameAndPostIdIn(String posterUsername, List<ObjectId> postIds);

    Optional<Rating> findByPosterUsernameAndPostId(String posterUsername, ObjectId postId);

}
