package com.dorohedoro.controller;

import com.dorohedoro.annotation.IgnoreResponseData;
import com.dorohedoro.entity.User;
import com.dorohedoro.service.IAuthService;
import com.dorohedoro.vo.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService jwtService;

    @IgnoreResponseData
    @PostMapping("/login")
    public JwtToken login(@RequestBody User userBO) throws Exception {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(jwtService.login(userBO));
        return jwtToken;
    }

    @IgnoreResponseData
    @PostMapping("/register")
    public JwtToken register(@RequestBody User userBO) throws Exception {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(jwtService.register(userBO));
        return jwtToken;
    }
}
