package com.dorohedoro.service.feign;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        value = "auth", // SERVICE ID
        //fallback = AuthOpenFeignFallbackService.class
        fallbackFactory = AuthOpenFeignFallbackFactory.class
)
public interface AuthOpenFeignService {

    @PostMapping(value = "/auth/checkin/login",
            consumes = "application/json",
            produces = "application/json")
    public ResponseBean login(@RequestBody UserDTO userDTO);
}
