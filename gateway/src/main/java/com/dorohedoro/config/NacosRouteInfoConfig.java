package com.dorohedoro.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.dorohedoro.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

@Slf4j
@Configuration
public class NacosRouteInfoConfig {

    public static final long DEFAULT_TIMEOUT = 30000;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    public String NACOS_SERVER_ADDR;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    public String NACOS_NAMESPACE;

    @Value("${nacos.gateway.route.config.data-id}")
    public String NACOS_ROUTE_DATA_ID;

    @Value("${nacos.gateway.route.config.group}")
    public String NACOS_ROUTE_GROUP;

    // Nacos配置服务
    private ConfigService configService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private Listener nacosConfigListener;

    @PostConstruct
    public void init() {
        try {
            log.info("init nacos config service...");
            configService = initConfigService();
            if (configService == null) {
                return;
            }

            String routeInfoStr = configService.getConfig(NACOS_ROUTE_DATA_ID, NACOS_ROUTE_GROUP, DEFAULT_TIMEOUT);
            log.info("get route info from nacos: {}", routeInfoStr);

            log.info("add route info...");
            List<RouteDefinition> routeInfo = JSON.parseArray(routeInfoStr, RouteDefinition.class);
            if (!CollectionUtils.isEmpty(routeInfo)) {
                routeService.addRouteInfo(routeInfo);
            }

            log.info("add config listener...");
            configService.addListener(NACOS_ROUTE_DATA_ID, NACOS_ROUTE_GROUP, nacosConfigListener);
        } catch (NacosException e) {
            log.info("gateway route info init error: {}", e.getErrMsg(), e);
        }
    }

    public ConfigService initConfigService() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", NACOS_SERVER_ADDR);
            properties.setProperty("namespace", NACOS_NAMESPACE);
            return NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            log.info("init config service error: {}", e.getErrMsg(), e);
            return null;
        }
    }


}
