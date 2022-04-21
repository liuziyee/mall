package com.dorohedoro.service;

import com.dorohedoro.entity.User;
import com.dorohedoro.vo.UsernameAndPassword;

public interface IJWTService {
    String generateToken(String username, String password) throws Exception;
    
    String generateToken(String username, String password, int expire) throws Exception;

    String registerAndGenerateToken(User user) throws Exception;
}
