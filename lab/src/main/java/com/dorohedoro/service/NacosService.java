package com.dorohedoro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
}
