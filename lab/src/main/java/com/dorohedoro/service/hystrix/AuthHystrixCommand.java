package com.dorohedoro.service.hystrix;

import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.exception.BizException;
import com.dorohedoro.service.feign.AuthOpenFeignService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class AuthHystrixCommand {

    @Resource
    private AuthOpenFeignService authOpenFeignService;

    @HystrixCommand(
            groupKey = "Auth",
            commandKey = "LoginCommand",
            threadPoolKey = "Auth",
            fallbackMethod = "loginFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1500"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10"),
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),
                    @HystrixProperty(name = "maxQueueSize", value = "101"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")
            }
    )
    public ResponseBean login(UserDTO userDTO) {
        log.info("use hystrix annotation to call auth service...");
        return authOpenFeignService.login(userDTO);
    }

    public ResponseBean loginFallback(UserDTO userDTO) {
        log.warn("something goes wrong when calling auth service, trigger hystrix fallback...");
        throw new BizException(ResCode.service_error);
    }
}
