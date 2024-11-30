package com.screenvault.screenvaultAPI.post;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PostAsyncService {

    private final PostRepository postRepository;

    public PostAsyncService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Async
    public void incrementViewCountAndSave(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        try {
            postRepository.save(post);
        }
        catch (Exception ignore) {}
    }

    @Async
    public void incrementReportCountAndSave(Post post) {
        post.setReportCount(post.getReportCount() + 1);
        try {
            postRepository.save(post);
        }
        catch (Exception ignore) {}
    }

}
