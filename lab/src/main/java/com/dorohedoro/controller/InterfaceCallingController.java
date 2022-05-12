package com.dorohedoro.controller;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.service.feign.AuthOpenFeignService;
import com.dorohedoro.service.RestTemplateService;
import com.dorohedoro.service.RibbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class InterfaceCallingController {

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private RibbonService ribbonService;

    @Resource
    private AuthOpenFeignService authOpenFeignService;
    
    @PostMapping("/rest/login")
    public ResponseBean loginByRest(@RequestBody UserDTO userDTO) {
        return restTemplateService.login(userDTO);
    }
    
    @PostMapping("/ribbon/login")
    public ResponseBean loginByRibbon(@RequestBody UserDTO userDTO) {
        return ribbonService.login(userDTO);
    }
    
    @PostMapping("/feign/login")
    public ResponseBean loginByFeign(@RequestBody UserDTO userDTO) {
        return authOpenFeignService.login(userDTO);
    }
}
