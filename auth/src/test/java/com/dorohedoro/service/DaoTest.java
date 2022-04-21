package com.dorohedoro.service;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.dorohedoro.dao.UserDao;
import com.dorohedoro.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class DaoTest {
    @Autowired
    private UserDao userDao;
    
    @Test
    public void createUserRecord() {
        User user = new User();
        user.setUsername("liuziye");
        user.setPassword(MD5.create().digestHex("1994"));
        user.setExtraInfo("{}");
        log.info("user record: {}", JSON.toJSONString(userDao.save(user)));
    }
}
