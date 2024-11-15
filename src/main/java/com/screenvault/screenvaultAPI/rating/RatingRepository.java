package com.screenvault.screenvaultAPI.rating;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends MongoRepository<Rating, ObjectId> {
    List<Rating> findByIdIn(List<RatingKey> ids);

    Optional<Rating> findById(RatingKey id);
}
