package com.no1.recipick.crawler.service.storage;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:recipe-images}")
    private String bucketName;

    public String uploadImageFromUrl(String imageUrl) {
        try {
            // URL에서 이미지 다운로드
            byte[] imageBytes = downloadImageFromUrl(imageUrl);

            // 파일명 생성
            String fileName = generateFileName(imageUrl);

            // MinIO에 업로드
            return uploadToMinio(imageBytes, fileName);

        } catch (Exception e) {
            log.error("Failed to upload image from URL: {}", imageUrl, e);
            throw new RuntimeException("Image upload failed", e);
        }
    }

    private byte[] downloadImageFromUrl(String imageUrl) throws IOException {
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            return inputStream.readAllBytes();
        }
    }

    private String generateFileName(String originalUrl) {
        String extension = getFileExtension(originalUrl);
        return UUID.randomUUID().toString() + extension;
    }

    private String getFileExtension(String url) {
        if (url.toLowerCase().contains(".jpg") || url.toLowerCase().contains(".jpeg")) {
            return ".jpg";
        } else if (url.toLowerCase().contains(".png")) {
            return ".png";
        } else if (url.toLowerCase().contains(".gif")) {
            return ".gif";
        }
        return ".jpg"; // 기본값
    }

    private String uploadToMinio(byte[] imageBytes, String fileName) {
        try {
            // MinIO에 객체 업로드
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(new ByteArrayInputStream(imageBytes), imageBytes.length, -1)
                    .contentType("image/jpeg")
                    .build()
            );

            // 업로드된 파일의 URL 반환
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .expiry(7, TimeUnit.DAYS)
                    .build()
            );

        } catch (Exception e) {
            log.error("Failed to upload image to MinIO: {}", fileName, e);
            throw new RuntimeException("MinIO upload failed", e);
        }
    }
}