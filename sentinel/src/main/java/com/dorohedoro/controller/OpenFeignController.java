package com.dorohedoro.controller;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.service.LabOpenFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class OpenFeignController {

    @Resource
    private LabOpenFeignService labOpenFeignService;

    @GetMapping("/feign/{id}")
    public ResponseBean getServiceInstance(@PathVariable String id) {
        return labOpenFeignService.getServiceInstance(id);
    }
}
