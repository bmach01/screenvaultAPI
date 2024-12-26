package com.screenvault.screenvaultAPI.image;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class ImageRepositoryImpl implements ImageRepository {

    public static final String SERVER_URL = System.getenv("SCREENVAULT_MINIO_URL");
    private static final String ACCESS_KEY = System.getenv("SCREENVAULT_MINIO_ACCESS_KEY");
    private static final String SECRET_KEY = System.getenv("SCREENVAULT_MINIO_SECRET_KEY");

    private static final MinioClient minioClient = MinioClient.builder()
            .endpoint(SERVER_URL)
            .credentials(ACCESS_KEY, SECRET_KEY)
            .build();

    public ImageRepositoryImpl() {
    }

    @Override
    public void saveImage(String name, String bucket, MultipartFile image) throws InternalError {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(name)
                            .contentType(image.getContentType())
                            .stream(image.getInputStream(), image.getSize(), -1)
                            .build());
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    @Override
    public void deleteImage(String name, String bucket) throws InternalError {
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

    @Override
    public void copyImageTo(String name, String from, String to) throws InternalError {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(to)
                            .object(name)
                            .source(
                                    CopySource.builder()
                                            .bucket(from)
                                            .object(name)
                                            .build()
                            )
                            .build()
            );
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public String getPresignedUrl(String name, String bucket) throws InternalError {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .method(Method.GET)
                            .object(name)
                            .build()
            );
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }
}
