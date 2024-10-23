package com.screenvault.screenvaultAPI.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PostRepository extends PagingAndSortingRepository<Post, UUID> {

    // "[...] One example is the findAll method, which requires querying all nodes in the cluster.
    // Such queries are not recommended with large datasets, because they can impact performance."
//    Page<Post> findAll(Pageable pageable); <-- exists by default
    Page<Post> findAllByTitle(String title, Pageable pageable);

    Page<Post> findAllByTag(String tag, Pageable pageable);

}
