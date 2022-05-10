package com.dorohedoro.controller;

import com.dorohedoro.service.HystrixAnnotationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hystrix")
public class HystrixController {

    @Autowired
    private HystrixAnnotationService hystrixAnnotationService;
    
    @GetMapping("/annotation/{id}")
    public List<ServiceInstance> getServiceInstance(@PathVariable String id) {
        log.info("call nacos service by postman...");
        log.info("service id: {}, thread: {}", id, Thread.currentThread().getName());
        return hystrixAnnotationService.getServiceInstance(id);
    }
    
}
