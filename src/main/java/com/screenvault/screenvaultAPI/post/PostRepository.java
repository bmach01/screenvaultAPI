package com.screenvault.screenvaultAPI.post;

import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.Set;

public interface PostRepository extends PagingAndSortingRepository<Post, ObjectId>, MongoRepository<Post, ObjectId> {

    @Nullable
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    @Nullable
    Page<Post> findByTagsContaining(Set<String> tags, Pageable pageable);

    @Nullable
    @Query("{ 'postedOn' : { $gte: ?0, $lte: ?1 } }")
    Page<Post> findByCreatedAtBetweenOrderByPopularityDesc(Date startDate, Date endDate, Pageable pageable);

    @Nullable
    Page<Post> findByTagsIn(Set<String> tags, Pageable pageable);

}
