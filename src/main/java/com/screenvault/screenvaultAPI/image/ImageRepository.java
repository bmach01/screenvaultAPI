package com.screenvault.screenvaultAPI.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageRepository {
    void saveImage(String name, String bucket, MultipartFile image) throws InternalError;
    void deleteImage(String name, String bucket) throws InternalError;
    void copyImageTo(String name, String from, String to) throws InternalError;
    String getPresignedUrl(String name, String bucket) throws InternalError;
}
