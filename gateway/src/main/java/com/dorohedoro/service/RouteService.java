package com.dorohedoro.service;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.constant.ResCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class RouteService implements ApplicationEventPublisherAware {
    @Autowired
    private RouteDefinitionWriter routeWriter;

    @Autowired
    private RouteDefinitionLocator routeLocator;

    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    public Long addRouteInfo(List<RouteDefinition> routeInfo) {
        try {
            List<RouteDefinition> existsRouteList = routeLocator.getRouteDefinitions().buffer().blockFirst();
            if (!CollectionUtils.isEmpty(existsRouteList)) {
                existsRouteList.forEach(route -> routeWriter.delete(Mono.just(route.getId())).subscribe());
            }
            routeInfo.forEach(route -> routeWriter.save(Mono.just(route)).subscribe());
            eventPublisher.publishEvent(new RefreshRoutesEvent(this));
            log.info("add route info success: {}", JSON.toJSONString(routeInfo));
            return ResCode.success.getCode();
        } catch (Exception e) {
            log.info("add route info error: {}", e.getMessage(), e);
            return ResCode.add_route_fail.getCode();
        }
    }
}
