package com.dorohedoro.service.hystrix;

import com.dorohedoro.service.NacosService;
import com.netflix.hystrix.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collections;
import java.util.List;

import static com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;

@Slf4j
public class HystrixCommandImpl extends com.netflix.hystrix.HystrixCommand<List<ServiceInstance>> {

    private final NacosService nacosService;
    
    private final String serviceId;
    
    public HystrixCommandImpl(NacosService nacosService, String serviceId) {
        super(
                HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Lab"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetServiceInstanceCommand"))
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("Lab"))
                        .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter().
                                        withExecutionIsolationStrategy(THREAD)
                                        .withFallbackEnabled(true)
                                        .withCircuitBreakerEnabled(true)
                                        .withExecutionTimeoutInMilliseconds(1500)
                                        .withCircuitBreakerRequestVolumeThreshold(10)
                                        .withCircuitBreakerErrorThresholdPercentage(50)
                                        .withCircuitBreakerSleepWindowInMilliseconds(5000))
        );
        
        this.serviceId = serviceId;
        this.nacosService = nacosService;
    }

    @Override
    protected List<ServiceInstance> run() throws Exception {
        log.info("use hystrix command to call nacos service...");
        log.info("service id: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return this.nacosService.getServiceInstance(serviceId);
    }

    @Override
    protected List<ServiceInstance> getFallback() {
        log.warn("something goes wrong when calling nacos service, trigger hystrix fallback...");
        log.warn("service id: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return Collections.emptyList();
    }
}
