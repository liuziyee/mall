package com.dorohedoro.service.hystrix.cache;

import com.dorohedoro.service.NacosService;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collections;
import java.util.List;

@Slf4j
public class NacosUseCacheHystrixCommand extends HystrixCommand<List<ServiceInstance>> {

    private final NacosService nacosService;

    private final String serviceId;

    private static final HystrixCommandKey COMMAND_KEY = HystrixCommandKey.Factory.asKey("getServiceInstance");

    public NacosUseCacheHystrixCommand(NacosService nacosService, String serviceId) {
        super(
                Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("lab"))
                        .andCommandKey(COMMAND_KEY)
        );
        
        this.serviceId = serviceId;
        this.nacosService = nacosService;
    }

    @Override
    protected List<ServiceInstance> run() throws Exception {
        log.info("use hystrix cache and command to call nacos service...");
        log.info("service id: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return this.nacosService.getServiceInstance(serviceId);
    }

    @Override
    protected List<ServiceInstance> getFallback() {
        log.warn("something goes wrong when calling nacos service, trigger hystrix fallback...");
        log.warn("service id: {}, thread: {}", serviceId, Thread.currentThread().getName());
        return Collections.emptyList();
    }

    @Override
    protected String getCacheKey() {
        return serviceId;
    }

    public static void flushCache(String serviceId) {
        log.info("flush cache...");
        HystrixRequestCache.getInstance(COMMAND_KEY, HystrixConcurrencyStrategyDefault.getInstance())
                .clear(serviceId);
    }
}
