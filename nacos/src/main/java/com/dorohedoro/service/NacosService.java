package com.dorohedoro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NacosService {
    @Autowired
    private DiscoveryClient discoveryClient;

    public List<ServiceInstance> getServiceInstance(String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }
}
