package com.dorohedoro.controller;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.service.rest.AuthOpenFeignService;
import com.dorohedoro.service.rest.RestTemplateService;
import com.dorohedoro.service.rest.RibbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InterfaceCallingController {

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private RibbonService ribbonService;

    @Autowired
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
