package com.dorohedoro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@ServletComponentScan
//@EnableCircuitBreaker
//@EnableDiscoveryClient
//@EnableFeignClients
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class LabApplication {
    public static void main(String[] args) {
        SpringApplication.run(LabApplication.class, args);
    }
}
