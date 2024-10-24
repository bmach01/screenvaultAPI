package com.screenvault.screenvaultAPI.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Set;
import java.util.UUID;

public interface PostRepository extends PagingAndSortingRepository<Post, UUID> {

    Page<Post> findByTitleContaining(String title, Pageable pageable);

    Page<Post> findByTagsContaining(Set<String> tags, Pageable pageable);

}
