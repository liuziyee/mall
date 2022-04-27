package com.dorohedoro.unit;

import com.dorohedoro.constant.AuthConstant;
import com.dorohedoro.entity.User;
import com.dorohedoro.service.ICheckInService;
import com.dorohedoro.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class JWTTest {
    @Autowired
    private ICheckInService authService;
    @Test
    public void generateTokenAndDecryptData() throws Exception {
        User user = new User();
        user.setUsername("liuziye");
        user.setPassword("008bd5ad93b754d500338c253d9c1770");

        String token = authService.login(user);
        log.info("token: {}", token);

        Claims claims = JWTUtil.decryptData(token);
        log.info("decrypt data: {}", claims.get(AuthConstant.KEY));
    } 
}
