package com.dorohedoro.service;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Slf4j
@Service
public class NacosService {
    @Autowired
    private DiscoveryClient discoveryClient;

    public List<ServiceInstance> getServiceInstance(String serviceId) {
        //try {
        //    TimeUnit.SECONDS.sleep(2);
        //} catch (InterruptedException e) {
        //}
        return discoveryClient.getInstances(serviceId);
    }

    public List<List<ServiceInstance>> getServiceInstanceBatch(List<String> serviceIds) {
        List<List<ServiceInstance>> batchServiceInstanceList = new ArrayList();
        serviceIds.forEach(id -> batchServiceInstanceList.add(discoveryClient.getInstances(id)));
        return batchServiceInstanceList;
    }

    @HystrixCollapser(
            batchMethod = "getServiceBatch",
            collapserKey = "GetServiceCommand",
            collapserProperties = {
                    @HystrixProperty(name = "timerDelayInMilliseconds", value = "300")
            }
    )
    public Future<List<ServiceInstance>> getService(String serviceId) {
        log.info("getService is executed, something goes wrong...");
        return null;
    }

    @HystrixCommand
    public List<List<ServiceInstance>> getServiceBatch(List<String> serviceIds) {
        log.info("use hystrix merge and annotation to call nacos service...");
        log.info("service id: {}, thread: {}", JSON.toJSONString(serviceIds), Thread.currentThread().getName());
        
        return this.getServiceInstanceBatch(serviceIds);
    }

}
