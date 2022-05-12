package com.dorohedoro.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.handler.GlobalBlockHandler;
import com.dorohedoro.service.LabOpenFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class FlowByDashboardController {

    @Resource
    private LabOpenFeignService labOpenFeignService;
    
    @GetMapping("/by-dashboard/{id}")
    @SentinelResource(
            value = "byDashboard",
            blockHandlerClass = GlobalBlockHandler.class,
            blockHandler = "byDashboardBlockHandler"
    )
    public ResponseBean byDashboard(@PathVariable String id) {
        return labOpenFeignService.getServiceInstance(id);
    }
}
