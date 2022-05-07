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

    //@Autowired
    //private LoadBalancerClient loadBalancerClient;
    //@Autowired
    //private RestTemplate restTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String uriPath = request.getURI().getPath();
        log.info("uriPath: {}", uriPath);
        if (uriPath.contains(GatewayConstant.LOGIN_URI) || uriPath.contains(GatewayConstant.REGISTER_URI)) {
            //String urlFormat = uriPath.contains(GatewayConstant.LOGIN_URI) ?
            //        GatewayConstant.AUTH_LOGIN_URL_FORMAT :
            //        GatewayConstant.AUTH_REGISTER_URL_FORMAT;
            //String token = getTokenFromAuth(request, urlFormat);
            //
            //response.getHeaders().add("token", token == null ? null : token);
            //response.setStatusCode(HttpStatus.OK);
            //return response.setComplete();
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

    //private String getRequestBodyData(ServerHttpRequest request) {
    //    Flux<DataBuffer> body = request.getBody();
    //    AtomicReference<String> bodyRef = new AtomicReference<>();
    //
    //    body.subscribe(buffer -> {
    //        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
    //        DataBufferUtils.release(buffer);
    //        bodyRef.set(charBuffer.toString());
    //    });
    //
    //    return bodyRef.get();
    //}
    //
    //private String getTokenFromAuth(ServerHttpRequest request, String urlFormat) {
    //    ServiceInstance authServiceInstance = loadBalancerClient.choose(AuthConstant.AUTH_SERVICE_ID);
    //    log.info(String.format("auth service instance info: [service id: %s, instance id: %s, metadata: %s",
    //            authServiceInstance.getServiceId(), authServiceInstance.getInstanceId(), authServiceInstance.getMetadata()));
    //
    //    String requestURL = String.format(urlFormat, authServiceInstance.getHost(), authServiceInstance.getPort());
    //    String requestBodyData = getRequestBodyData(request);
    //
    //    HttpHeaders headers = new HttpHeaders();
    //    headers.setContentType(MediaType.APPLICATION_JSON);
    //    JwtToken token = restTemplate.postForObject(
    //            requestURL, 
    //            new HttpEntity<>(requestBodyData, headers), 
    //            JwtToken.class);
    //
    //    if (token != null) {
    //        return token.getToken();
    //    }
    //    return null;
    //}
    
}
