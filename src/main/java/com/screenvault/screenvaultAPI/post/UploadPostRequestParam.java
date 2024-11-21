package com.screenvault.screenvaultAPI.post;

import org.springframework.web.multipart.MultipartFile;

public record UploadPostRequestParam(
        Post post,
        boolean isPublic,
        MultipartFile image
) {
}
