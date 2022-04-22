package com.dorohedoro.controller;

import com.dorohedoro.annotation.IgnoreResponseData;
import com.dorohedoro.entity.User;
import com.dorohedoro.service.ICheckInService;
import com.dorohedoro.vo.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkin")
public class CheckInController {

    @Autowired
    private ICheckInService checkInService;
    
    @IgnoreResponseData
    @PostMapping("/login")
    public JwtToken login(@RequestBody User userBO) throws Exception {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(checkInService.login(userBO));
        return jwtToken;
    }

    @IgnoreResponseData
    @PostMapping("/register")
    public JwtToken register(@RequestBody User userBO) throws Exception {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(checkInService.register(userBO));
        return jwtToken;
    }
}
