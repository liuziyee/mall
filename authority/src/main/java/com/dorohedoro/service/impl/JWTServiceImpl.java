package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.constant.AuthConstant;
import com.dorohedoro.constant.CommonConstant;
import com.dorohedoro.dao.UserDao;
import com.dorohedoro.dto.UserDto;
import com.dorohedoro.entity.User;
import com.dorohedoro.exception.BizException;
import com.dorohedoro.service.IJWTService;
import com.dorohedoro.util.BeanUtil;
import com.dorohedoro.util.ResCode;
import com.dorohedoro.vo.UsernameAndPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class JWTServiceImpl implements IJWTService {
    @Autowired
    private UserDao userDao;
    
    @Override
    public String generateToken(String username, String password) throws Exception {
        return generateToken(username, password, 0);
    }

    @Override
    public String generateToken(String username, String password, int expire) throws Exception {
        User user = userDao.findByUsernameAndPassword(username, password);
        if (user == null) {
            throw new BizException(ResCode.login);
        }

        if (expire <= 0) {
            expire = AuthConstant.DEFAULT_EXPIRE_DAY;
        }

        UserDto userDto = BeanUtil.copy(user, UserDto.class);

        // 计算过期时间
        ZonedDateTime zonedDateTime = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                .atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zonedDateTime.toInstant());

        return Jwts.builder()
                .claim(CommonConstant.JWT_USER_INFO_KEY, JSON.toJSONString(userDto))
                .setId(UUID.randomUUID().toString())
                .setExpiration(expireDate)
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String registerAndGenerateToken(User userBO) throws Exception {
        User oldUser = userDao.findByUsername(userBO.getUsername());
        if (oldUser != null) {
            throw new BizException(ResCode.user_exists);
        }

        User user = new User();
        user.setUsername(userBO.getUsername());
        user.setPassword(userBO.getPassword());
        user.setExtraInfo("{}");

        user = userDao.save(user);
        log.info("register success: {}", JSON.toJSONString(user));
        return generateToken(user.getUsername(), user.getPassword());
    }

    public PrivateKey getPrivateKey() throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(AuthConstant.PRIVATE_KEY));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }
}
