package com.dorohedoro.service;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "auth") // SERVICE ID
public interface AuthOpenFeignService {

    @PostMapping(value = "/auth/checkin/login", 
            consumes = "application/json", 
            produces = "application/json")
    public ResponseBean login(UserDTO userDTO);
}
