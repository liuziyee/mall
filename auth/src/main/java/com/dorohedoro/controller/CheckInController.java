package com.dorohedoro.controller;

import com.dorohedoro.entity.User;
import com.dorohedoro.service.ICheckInService;
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
    
    @PostMapping("/login")
    public String login(@RequestBody User userBO) throws Exception {
        return checkInService.login(userBO);
    }

    @PostMapping("/register")
    public String register(@RequestBody User userBO) throws Exception {
        return checkInService.register(userBO);
    }
}
