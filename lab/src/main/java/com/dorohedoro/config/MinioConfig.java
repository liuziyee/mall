package com.dorohedoro.config;

import com.dorohedoro.props.OssProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MinioClient.class)
@ConditionalOnProperty(
        value = "oss.service",
        havingValue = "minio"
)
@EnableConfigurationProperties(OssProperties.class)
public class MinioConfig {

    @Autowired
    private OssProperties ossProperties;

    @Bean
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient minioClient() {
        return MinioClient.builder().
                endpoint(ossProperties.getEndpoint()).
                credentials(ossProperties.getAccessKey(), ossProperties.getSecretKey())
                .build();
    }
}
