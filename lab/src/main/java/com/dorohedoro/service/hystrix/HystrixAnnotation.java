package com.dorohedoro.service.hystrix;

import com.dorohedoro.service.NacosService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class HystrixAnnotation {
    
    @Autowired
    private NacosService nacosService;

    @HystrixCommand(
            groupKey = "Lab",
            commandKey = "GetServiceInstanceCommand",
            threadPoolKey = "Lab",
            fallbackMethod = "getServiceInstanceFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1500"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10"),
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),
                    @HystrixProperty(name = "maxQueueSize", value = "101"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")
            }
    )
    public List<ServiceInstance> getServiceInstance(String serviceId) {
        log.info("use hystrix annotation to call nacos service...");
        log.info("service id: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return nacosService.getServiceInstance(serviceId);
    }

    public List<ServiceInstance> getServiceInstanceFallback(String serviceId) {
        log.warn("something goes wrong when calling nacos service, trigger hystrix fallback...");
        return Collections.emptyList();
    }
}
