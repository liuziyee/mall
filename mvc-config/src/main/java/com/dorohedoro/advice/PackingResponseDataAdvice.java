package com.dorohedoro.advice;

import com.dorohedoro.annotation.IgnoreResponseData;
import com.dorohedoro.vo.ResponseBean;
import com.dorohedoro.constant.ResCode;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class PackingResponseDataAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseData.class)) {
            return false;
        }
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseData.class)) {
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
        } else if (o instanceof ResponseBean) {
            resBean = (ResponseBean) o;
        } else {
            resBean.setData(o);
        }

        return resBean;
    }
}
