package com.dorohedoro.com.dorohedoro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    public static final long DEFAULT_TIMEOUT = 30000;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    public static String NACOS_SERVER_ADDR;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    public static String NACOS_NAMESPACE;

    @Value("${nacos.gateway.route.config.data-id}")
    public static String NACOS_ROUTE_DATA_ID;

    @Value("${nacos.gateway.route.config.group}")
    public static String NACOS_ROUTE_GROUP;
}