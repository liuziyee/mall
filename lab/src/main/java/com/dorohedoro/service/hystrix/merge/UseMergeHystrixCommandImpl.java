package com.dorohedoro.service.hystrix.merge;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.service.NacosService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collections;
import java.util.List;

@Slf4j
public class UseMergeHystrixCommandImpl extends HystrixCommand<List<List<ServiceInstance>>> {

    private final NacosService nacosService;

    private final List<String> serviceIds;


    public UseMergeHystrixCommandImpl(NacosService nacosService, List<String> serviceIds) {
        super(
                HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Lab"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetServiceInstanceCommand"))
        );

        this.serviceIds = serviceIds;
        this.nacosService = nacosService;
    }

    @Override
    protected List<List<ServiceInstance>> run() throws Exception {
        log.info("use hystrix merge and command to call nacos service...");
        log.info("service id: {}, thread: {}", JSON.toJSONString(serviceIds), Thread.currentThread().getName());
        return this.nacosService.getServiceInstanceBatch(serviceIds);
    }

    @Override
    protected List<List<ServiceInstance>> getFallback() {
        return Collections.emptyList();
    }
}
