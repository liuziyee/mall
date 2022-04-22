package com.dorohedoro.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.listener.Listener;
import com.dorohedoro.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class NacosConfigListener implements Listener {
    @Autowired
    private RouteService routeService;
    
    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        log.info("receive route info: {}", configInfo);
        List<RouteDefinition> routeInfo = JSON.parseArray(configInfo, RouteDefinition.class);
        routeService.addRouteInfo(routeInfo);
    }
}
