package com.screenvault.screenvaultAPI.post;

import io.minio.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.security.InvalidKeyException;

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

    public String uploadPrivateImage(MultipartFile image, String name) throws InternalError {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(PRIVATE_BUCKET)
                            .object(name)
                            .contentType(image.getContentType())
                            .stream(image.getInputStream(), image.getSize(), -1)
                            .build());

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(PRIVATE_BUCKET)
                            .object(name)
                            .build()
            );
        }
        catch (InvalidKeyException e) {
            System.out.println(e.getMessage());
            throw new InternalError(e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void uploadPublicImage(MultipartFile image, String name) throws InternalError {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(PUBLIC_BUCKET)
                            .object(name)
                            .contentType(image.getContentType())
                            .stream(image.getInputStream(), image.getSize(), -1)
                            .build());
        }
        catch (InvalidKeyException e) {
            throw new InternalError(e.getMessage());
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void deleteImage(String name, boolean isPrivate) throws InternalError {
        if (isPrivate) deleteImageFromBucket(PRIVATE_BUCKET, name);
        else deleteImageFromBucket(PUBLIC_BUCKET, name);
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
        catch (InvalidKeyException e) {
            throw new InternalError(e.getMessage());
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void test() {
        try {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(PRIVATE_BUCKET)
                            .build()
            );
        }
        catch (InvalidKeyException e) {
            System.out.println(e.getMessage());
            throw new InternalError(e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public String updateImageToPrivate(String name) {
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

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(PRIVATE_BUCKET)
                            .object(name)
                            .build()
            );
        }
        catch (InvalidKeyException e) {
            throw new InternalError(e.getMessage());
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void upateImageToPublic(String name) {
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
        catch (InvalidKeyException e) {
            throw new InternalError(e.getMessage());
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }
}
