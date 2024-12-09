package com.screenvault.screenvaultAPI.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private static final String PUBLIC_BUCKET = "public";
    private static final String PRIVATE_BUCKET = "private";

    private final CustomImageRepository customImageRepository;

    public ImageService(CustomImageRepository customImageRepository) {
        this.customImageRepository = customImageRepository;
    }

    public void uploadPrivateImage(MultipartFile image, String name) throws InternalError {
        customImageRepository.saveImage(name, PRIVATE_BUCKET, image);
    }

    public void uploadPublicImage(MultipartFile image, String name) throws InternalError {
        customImageRepository.saveImage(name, PUBLIC_BUCKET, image);
    }

    public void deleteImage(String name, boolean isPublic) throws InternalError {
        if (isPublic)
            customImageRepository.deleteImage(name, PUBLIC_BUCKET);
        else
            customImageRepository.deleteImage(name, PRIVATE_BUCKET);
    }

    public void moveImageToPrivate(String name) throws InternalError {
        customImageRepository.copyImageTo(name, PUBLIC_BUCKET, PRIVATE_BUCKET);
    }

    public void moveImageToPublic(String name) throws InternalError {
        customImageRepository.copyImageTo(name, PRIVATE_BUCKET, PUBLIC_BUCKET);
    }

    public String getPrivateImageUrl(String name) throws InternalError {
        return customImageRepository.getPresignedUrl(name, PRIVATE_BUCKET);
    }

    public String getPublicImageUrl(String name) {
        return ImageRepositoryImpl.SERVER_URL + "/" + PUBLIC_BUCKET + "/" + name;
        // TODO: make it cleaner? referencing CustomImageRepositoryImpl here is absolutely disgusting
    }
}
