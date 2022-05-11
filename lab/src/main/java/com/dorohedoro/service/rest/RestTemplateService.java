package com.dorohedoro.service.rest;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.AuthConstant;
import com.dorohedoro.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RestTemplateService {
    
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    
    public ResponseBean login(UserDTO userDTO) {
        ServiceInstance serviceInstance = loadBalancerClient.choose(AuthConstant.AUTH_SERVICE_ID);

        log.info("auth service info: {}, {}, {}", 
                serviceInstance.getServiceId(), 
                serviceInstance.getInstanceId(),
                JSON.toJSONString(serviceInstance.getMetadata()));

        String url = String.format("http://%s:%s/auth/checkin/login", serviceInstance.getHost(), serviceInstance.getPort());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new RestTemplate().postForObject(
                url, 
                new HttpEntity<>(JSON.toJSONString(userDTO), headers), 
                ResponseBean.class);
    }
}
