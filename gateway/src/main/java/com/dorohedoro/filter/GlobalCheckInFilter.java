package com.dorohedoro.filter;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.constant.AuthConstant;
import com.dorohedoro.constant.GatewayConstant;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalCheckInFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String uriPath = request.getURI().getPath();
        log.info("URI: {}", uriPath);
        if (StringUtils.containsAny(uriPath,  
                GatewayConstant.LOGIN_URI,
                GatewayConstant.REGISTER_URI,
                GatewayConstant.ACTUATOR_URI)) {
            return chain.filter(exchange);
        }

        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(GatewayConstant.HEADER_TOKEN);
        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        try {
            Claims claims = JWTUtil.decryptData(token);
            UserDTO userDTO = JSON.parseObject(claims.get(AuthConstant.KEY).toString(), UserDTO.class);
            if (userDTO == null) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        } catch (Exception e) {
            log.info("decrypt JWT error: {}", e.getMessage(), e);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2;
    }
    
}
