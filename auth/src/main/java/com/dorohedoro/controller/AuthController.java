package com.dorohedoro.controller;

import com.dorohedoro.annotation.IgnoreResponseData;
import com.dorohedoro.entity.User;
import com.dorohedoro.service.IJWTService;
import com.dorohedoro.vo.JwtToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IJWTService jwtService;

    @IgnoreResponseData
    @RequestMapping("/login")
    public JwtToken login(@RequestBody User userBO) throws Exception {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(jwtService.generateToken(userBO));
        return jwtToken;
    }

    @IgnoreResponseData
    @RequestMapping("/register")
    public JwtToken register(@RequestBody User userBO) throws Exception {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(jwtService.registerAndGenerateToken(userBO));
        return jwtToken;
    }
}
