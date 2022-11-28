package com.gmagnotta.hello_tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Item;

public class S3Util {

    private MinioClient minioClient;

    public S3Util(String endpoint, String accessKey, String secretKey) {
        // For aws use https://s3.amazonaws.com
        // For minio use http://localhost:9000
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public void uploadFile(String bucket, String fileName, InputStream inputStream, String contentType)
            throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException,
            InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException,
            IllegalArgumentException, IOException {

        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucket).object(fileName).stream(
                        inputStream, -1, 10485760)
                        .contentType(contentType)
                        .build());
    }

    public Iterable<Result<Item>> listBucket(String bucket) {
        return minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).build());
    }
}