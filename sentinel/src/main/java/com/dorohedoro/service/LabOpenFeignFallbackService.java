package com.dorohedoro.service;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LabOpenFeignFallbackService implements LabOpenFeignService{
    @Override
    public ResponseBean getServiceInstance(String id) {
        log.warn("something goes wrong when calling getServiceInstance interface in lab service...");
        ResponseBean resBean = new ResponseBean();
        resBean.setCode(ResCode.service_error.getCode());
        resBean.setMsg(ResCode.service_error.getDesc());
        return resBean;
    }
}
