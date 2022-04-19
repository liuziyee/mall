package com.dorohedoro.controller;

import com.dorohedoro.service.NacosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nacos")
public class NacosController {
    @Autowired
    private NacosService nacosService;

    @GetMapping("/service-instance/{id}")
    public List<ServiceInstance> getServiceInstance(@PathVariable String id) {
        return nacosService.getServiceInstance(id);
    }
}
