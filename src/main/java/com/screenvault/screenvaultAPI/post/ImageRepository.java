package com.screenvault.screenvaultAPI.post;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class ImageRepository {
    private static final String BUCKET_NAME = "images";
    private static final String SERVER_URL = System.getenv("SCREENVAULT_MINIO_URL");
    private static final String ACCESS_KEY = System.getenv("SCREENVAULT_MINIO_ACCESS_KEY");
    private static final String SECRET_KEY = System.getenv("SCREENVAULT_MINIO_SECRET_KEY");

    private final MinioClient minioClient;

    public ImageRepository(MinioClient minioClient) {
        this.minioClient = MinioClient.builder()
                .endpoint(SERVER_URL)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();
    }

    public void uploadImage(MultipartFile image, String name)
        throws InternalError
    {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("images")
                            .object(name)
                            .contentType(image.getContentType())
                            .stream(image.getInputStream(), image.getSize(), -1)
                            .build());
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public void deleteImage(String name) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("images")
                            .object(name)
                            .build()
            );
        }
        catch (Exception e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }
}
