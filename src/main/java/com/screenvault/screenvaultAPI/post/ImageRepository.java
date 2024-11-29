package com.screenvault.screenvaultAPI.post;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class ImageRepository {
    private static final String PUBLIC_BUCKET = "public";
    private static final String PRIVATE_BUCKET = "private";
    private static final String SERVER_URL = System.getenv("SCREENVAULT_MINIO_URL");
    private static final String ACCESS_KEY = System.getenv("SCREENVAULT_MINIO_ACCESS_KEY");
    private static final String SECRET_KEY = System.getenv("SCREENVAULT_MINIO_SECRET_KEY");

    private final MinioClient minioClient = MinioClient.builder()
            .endpoint(SERVER_URL)
            .credentials(ACCESS_KEY, SECRET_KEY)
            .build();

    public ImageRepository() {
    }

    public void uploadPrivateImage(MultipartFile image, String name) throws InternalError {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(PRIVATE_BUCKET)
                            .object(name)
                            .contentType(image.getContentType())
                            .stream(image.getInputStream(), image.getSize(), -1)
                            .build());
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void uploadPublicImage(MultipartFile image, String name) throws InternalError {
        try {
            ObjectWriteResponse res = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(PUBLIC_BUCKET)
                            .object(name)
                            .contentType(image.getContentType())
                            .stream(image.getInputStream(), image.getSize(), -1)
                            .build());
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void deleteImage(String name, boolean isPublic) throws InternalError {
        if (isPublic) deleteImageFromBucket(PUBLIC_BUCKET, name);
        else deleteImageFromBucket(PRIVATE_BUCKET, name);
    }

    private void deleteImageFromBucket(String bucket, String name) throws InternalError {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(name)
                            .build()
            );
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void moveImageToPrivate(String name) throws InternalError {
        System.out.println("Will try to copy!");

        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(PRIVATE_BUCKET)
                            .object(name)
                            .source(
                                    CopySource.builder()
                                            .bucket(PUBLIC_BUCKET)
                                            .object(name)
                                            .build()
                            )
                            .build()
            );
            deleteImageFromBucket(PUBLIC_BUCKET, name);
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void moveImageToPublic(String name) throws InternalError {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(PUBLIC_BUCKET)
                            .object(name)
                            .source(
                                    CopySource.builder()
                                            .bucket(PRIVATE_BUCKET)
                                            .object(name)
                                            .build()
                            )
                            .build()
            );
            deleteImageFromBucket(PRIVATE_BUCKET, name);
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public String getPrivateImageUrl(String name) throws InternalError {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(PRIVATE_BUCKET)
                            .method(Method.GET)
                            .object(name)
                            .build()
            );
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public String getPublicImageUrl(String name) {
        return SERVER_URL + "/" + PUBLIC_BUCKET + "/" + name;
    }
}
