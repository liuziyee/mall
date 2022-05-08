package com.dorohedoro.advice;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.annotation.IgnorePackingResponseData;
import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.ResCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RestControllerAdvice
public class PackingResponseDataAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnorePackingResponseData.class)) {
            return false;
        }
        if (methodParameter.getMethod().isAnnotationPresent(IgnorePackingResponseData.class)) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, 
                                  MethodParameter methodParameter, 
                                  MediaType mediaType, 
                                  Class<? extends HttpMessageConverter<?>> aClass, 
                                  ServerHttpRequest serverHttpRequest, 
                                  ServerHttpResponse serverHttpResponse) {
        ResponseBean resBean = new ResponseBean();
        resBean.setCode(ResCode.success.getCode());

        if (o == null) {
            return resBean;
        } else if (o instanceof String) {
            resBean.setData(o);
            return JSON.toJSONString(resBean);
        } else if (o instanceof ResponseBean) {
            resBean = (ResponseBean) o;
        } else {
            resBean.setData(o);
        }

        log.info("uri: {}, data: {}", serverHttpRequest.getURI().getPath(), JSON.toJSONString(resBean));
        return resBean;
    }
}
