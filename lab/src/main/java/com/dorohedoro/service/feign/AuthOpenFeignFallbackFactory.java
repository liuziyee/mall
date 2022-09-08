package com.dorohedoro.service.feign;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.exception.BizException;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthOpenFeignFallbackFactory implements FallbackFactory<AuthOpenFeignService> {
    @Override
    public AuthOpenFeignService create(Throwable throwable) {
        log.warn("something goes wrong when calling login interface in auth service...");
        log.warn(throwable.getMessage(), throwable);

        return (userDTO) -> new ResponseBean()
                .setCode(ResCode.service_error.getCode())
                .setMsg(ResCode.service_error.getDesc());
    }
}
