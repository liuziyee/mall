package com.dorohedoro.service;

import com.dorohedoro.entity.User;
import com.dorohedoro.vo.UsernameAndPassword;

public interface IJWTService {
    String generateToken(User userBO) throws Exception;
    
    String generateToken(User userBO, int expire) throws Exception;

    String registerAndGenerateToken(User userBO) throws Exception;
}
