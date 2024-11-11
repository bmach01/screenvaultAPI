package com.screenvault.screenvaultAPI.rating;

import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RatingRepository extends MongoRepository<Rating, ObjectId> {

    @Nullable
    List<Rating> findByPosterUsernameAndPostIdIn(String posterUsername, List<ObjectId> postIds);

    @Nullable
    List<Rating> findByPosterUsernameAndPostId(String posterUsername, ObjectId postId);

}
