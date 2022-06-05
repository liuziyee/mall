package com.dorohedoro.unit;

import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.Instant;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MinioTest {

    @Autowired
    private MinioClient minioClient;

    @Test
    public void exists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("img").build());
        log.info("exists: {}", exists);
    }

    @Test
    public void upload() throws Exception {
        String bucket = "img";
        String object = Instant.now().toEpochMilli() + ".img";
        String path = "C:\\Git\\LICENSE.txt";
        
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucket)
                        .object(object)
                        .filename(path)
                        .build());
        log.info("{} is uploaded as {} to bucket {}.", path, object, bucket);
    }
    
    @Test
    public void download() throws Exception{
        String bucket = "img";
        String object = "1654411203723.img";
        String path = "C:\\Git\\MINIO.txt";  

        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucket)
                        .object(object)
                        .filename(path)
                        .build()
        );
        log.info("{} is downloaded to {}", object, path);
    }
}
