package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.constant.AuthConstant;
import com.dorohedoro.dao.UserDao;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.entity.User;
import com.dorohedoro.exception.BizException;
import com.dorohedoro.service.ICheckInService;
import com.dorohedoro.util.BeanUtil;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

@Service
@Transactional(rollbackFor = Exception.class)
public class CheckInServiceImpl implements ICheckInService {
    @Autowired
    private UserDao userDao;
    
    @Override
    public String login(User userBO) throws Exception {
        return login(userBO, 0);
    }

    @Override
    public String login(User userBO, Integer expire) throws Exception {
        User user = userDao.findByUsernameAndPassword(userBO.getUsername(), userBO.getPassword());
        if (user == null) {
            throw new BizException(ResCode.login);
        }

        UserDTO userDTO = BeanUtil.copy(user, UserDTO.class);

        return JWTUtil.generateToken(expire, JSON.toJSONString(userDTO));
    }

    @Override
    public String register(User userBO) throws Exception {
        User oldUser = userDao.findByUsername(userBO.getUsername());
        if (oldUser != null) {
            throw new BizException(ResCode.user_exists);
        }

        User user = new User();
        user.setUsername(userBO.getUsername());
        user.setPassword(userBO.getPassword());
        user.setExtraInfo("{}");
        user = userDao.save(user);
        
        UserDTO userDTO = BeanUtil.copy(user, UserDTO.class);
        return JWTUtil.generateToken(0, JSON.toJSONString(userDTO));
    }

    public PrivateKey getPrivateKey() throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(AuthConstant.PRIVATE_KEY)
        );
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
