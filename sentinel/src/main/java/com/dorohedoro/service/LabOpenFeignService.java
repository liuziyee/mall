package com.dorohedoro.service;

import com.dorohedoro.bean.ResponseBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        value = "lab",
        fallback = LabOpenFeignFallbackService.class
)
public interface LabOpenFeignService {

    @GetMapping(
            value = "/lab/nacos/service/{id}",
            consumes = "application/json",
            produces = "application/json")
    ResponseBean getServiceInstance(@PathVariable String id);
}
