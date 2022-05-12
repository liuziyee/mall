package com.dorohedoro.service.feign;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthOpenFeignFallbackService implements AuthOpenFeignService{
    
    @Override
    public ResponseBean login(UserDTO userDTO) {
        log.warn("something goes wrong when calling login interface in auth service...");
        ResponseBean resBean = new ResponseBean();
        resBean.setCode(ResCode.service_error.getCode());
        resBean.setMsg(ResCode.service_error.getDesc());
        return resBean;
    }
}
