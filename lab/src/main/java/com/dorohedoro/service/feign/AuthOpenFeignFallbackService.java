package com.dorohedoro.service.feign;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthOpenFeignFallbackService implements AuthOpenFeignService{
    
    @Override
    public ResponseBean login(UserDTO userDTO) {
        log.warn("something goes wrong when calling login interface in auth service...");
        return new ResponseBean()
                .setCode(ResCode.service_error.getCode())
                .setMsg(ResCode.service_error.getDesc());
    }
}
