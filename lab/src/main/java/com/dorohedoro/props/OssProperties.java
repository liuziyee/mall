package com.dorohedoro.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {
    private String service;
    private String endpoint;
    private String accessKey;
    private String secretKey;
}
