package com.dorohedoro.service;

import com.dorohedoro.entity.User;

public interface ICheckInService {
    String login(User userBO) throws Exception;
    
    String login(User userBO, Integer expire) throws Exception;

    String register(User userBO) throws Exception;
}
