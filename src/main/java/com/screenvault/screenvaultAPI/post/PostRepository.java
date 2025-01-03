package com.screenvault.screenvaultAPI.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PostRepository extends PostCustomRepository, PagingAndSortingRepository<Post, UUID>, MongoRepository<Post, UUID> {
    @Query(value = "{ 'isDeleted': { $ne: true }, 'isPublic': ?0 }",
          fields = "{ 'tags': 0, 'collectionIds': 0, 'reportCount': 0, 'isDeleted': 0 }",
          sort = "{ 'postedOn': -1 }")
    Page<Post> findAllByIsPublic(boolean isPublic, Pageable pageable);

    @Query(value = "{ 'isPublic': ?0, 'title': { $regex: ?1, $options: 'i' } }",
            fields = "{ 'tags': 0, 'collectionIds': 0, 'reportCount': 0, 'isDeleted': 0 }",
            sort = "{ 'postedOn': -1 }")
    Page<Post> findAllByIsPublicAndTitleContaining(boolean isPublic, String title, Pageable pageable);

    @Query(fields = "{ 'tags': 0, 'collectionIds': 0, 'reportCount': 0, 'isDeleted': 0 }", sort = "{ 'postedOn': -1 }")
    Page<Post> findAllByIsPublicAndTagsIn(boolean isPublic, Set<String> tags, Pageable pageable);

    @Query(value = "{ 'reportCount': { $gt: 0 }, 'isDeleted': { $ne: true } }", fields = "{ 'collectionIds': 0, 'isDeleted': 0 }",
            sort = "{ 'reportCount': -1 }")
    Page<Post> findByReportsGreaterThanZero(Pageable pageable);

    @Query(value = "{ 'isDeleted': { $ne: true }, '_id': ?0 }",
            fields = "{ 'collectionIds': 0, 'reportCount': 0, 'isDeleted': 0 }")
    Optional<Post> findById(UUID id);

    @Query(value = "{ 'collectionIds': ?0 }", fields = "{ 'tags': 0, 'collections': 0, 'reportCount': 0, 'isDeleted': 0 }")
    Page<Post> findAllByCollectionId(UUID collectionId, Pageable pageable);

}
