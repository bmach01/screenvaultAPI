package com.screenvault.screenvaultAPI.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private static final String PUBLIC_BUCKET = "public";
    private static final String PRIVATE_BUCKET = "private";
    private static final String[] VALID_IMAGE_TYPES = { "image/jpeg", "image/png", "image/svg+xml", "image/webp" };

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void uploadImage(String name, MultipartFile image, boolean isPublic) throws InternalError, IllegalArgumentException {
        if (!isValidImageType(image.getContentType()))
            throw new IllegalArgumentException("Image type not supported.");

        if (isPublic)
            imageRepository.saveImage(name, PUBLIC_BUCKET, image);
        else
            imageRepository.saveImage(name, PRIVATE_BUCKET, image);
    }

    public void deleteImage(String name, boolean isPublic) throws InternalError {
        if (isPublic)
            imageRepository.deleteImage(name, PUBLIC_BUCKET);
        else
            imageRepository.deleteImage(name, PRIVATE_BUCKET);
    }

    public void moveImageToPrivate(String name) throws InternalError {
        imageRepository.copyImageTo(name, PUBLIC_BUCKET, PRIVATE_BUCKET);
    }

    public void moveImageToPublic(String name) throws InternalError {
        imageRepository.copyImageTo(name, PRIVATE_BUCKET, PUBLIC_BUCKET);
    }

    // Although logic is overlapping it is more versatile and disposes of if-else structure further on
    public String getImageUrl(String name, boolean isPublic) throws InternalError {
        if (isPublic) return ImageRepositoryImpl.SERVER_URL + "/" + PUBLIC_BUCKET + "/" + name;
        return imageRepository.getPresignedUrl(name, PRIVATE_BUCKET);
    }

    public static String getPublicImageUrl(String name) {
        return ImageRepositoryImpl.SERVER_URL + "/" + PUBLIC_BUCKET + "/" + name;
    }

    private boolean isValidImageType(String type) {
        for (String valid : VALID_IMAGE_TYPES) if (type.equals(valid)) return true;
        return false;
    }
}
