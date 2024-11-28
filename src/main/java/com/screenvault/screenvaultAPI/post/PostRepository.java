package com.screenvault.screenvaultAPI.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PostRepository extends PagingAndSortingRepository<Post, UUID>, MongoRepository<Post, UUID> {
    Optional<Page<Post>> findAllByIsPublic(boolean isPublic, Pageable pageable);

    Optional<Page<Post>> findByIsPublicAndTitleContaining(boolean isPublic, String title, Pageable pageable);

    Optional<Page<Post>> findByIsPublicAndTagsContaining(boolean isPublic, Set<String> tags, Pageable pageable);

    @Query("{ 'postedOn' : { $gte: ?0, $lte: ?1 } }")
    Optional<Page<Post>> findByCreatedAtBetweenOrderByPopularityDesc(Date startDate, Date endDate, Pageable pageable);

    Optional<Page<Post>> findByIsPublicAndTagsIn(boolean isPublic, Set<String> tags, Pageable pageable);

    @Query("{ 'reports': { $gt: 0 } }")
    Optional<Page<Post>> findByReportsGreaterThanZero(Pageable pageable);
}
