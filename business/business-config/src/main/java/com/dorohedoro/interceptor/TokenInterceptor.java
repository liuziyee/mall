package com.dorohedoro.interceptor;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.constant.AuthConstant;
import com.dorohedoro.constant.GatewayConstant;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.context.UserContext;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.exception.BizException;
import com.dorohedoro.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader(GatewayConstant.HEADER_TOKEN);
        try {
            Claims claims = JWTUtil.decryptData(token);
            UserDTO userDTO = JSON.parseObject(claims.get(AuthConstant.KEY).toString(), UserDTO.class);
            log.info("put user data into ThreadLocal: {}", userDTO);
            UserContext.setUserData(userDTO);
        } catch (Exception e) {
            log.error("decrypt JWT error: {}", e.getMessage(), e);
            throw new BizException(ResCode.unauthorized);
        }
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clearUserData();
    }
}
