package com.dorohedoro.service.rest;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.AuthConstant;
import com.dorohedoro.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RibbonService {

    @Autowired
    private RestTemplate restTemplate;
    
    public ResponseBean login(UserDTO userDTO) {
        String url = String.format("http://%s/auth/checkin/login", AuthConstant.AUTH_SERVICE_ID);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForObject(
                url,
                new HttpEntity<>(JSON.toJSONString(userDTO), headers),
                ResponseBean.class);
    }
}
