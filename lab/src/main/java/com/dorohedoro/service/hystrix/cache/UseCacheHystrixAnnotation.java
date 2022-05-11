package com.dorohedoro.service.hystrix.cache;

import com.dorohedoro.service.NacosService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UseCacheHystrixAnnotation {

    @Autowired
    private NacosService nacosService;

    @CacheResult
    @HystrixCommand(commandKey = "getServiceInstance")
    public List<ServiceInstance> getServiceInstance(@CacheKey String serviceId) {
        log.info("use hystrix cache and annotation to call nacos service...");
        log.info("service id: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return nacosService.getServiceInstance(serviceId);
    }

    @CacheRemove(commandKey = "getServiceInstance")
    @HystrixCommand
    public void clearCache(@CacheKey String cacheKey) {
        log.info("flush cache...");
    }
}
