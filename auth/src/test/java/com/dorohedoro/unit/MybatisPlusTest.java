package com.dorohedoro.unit;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dorohedoro.entity.User;
import com.dorohedoro.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MybatisPlusTest {
    @Autowired
    private UserMapper userMapper;
    
    @Test
    public void createRecord() {
        User user = new User();
        user.setUsername("liuziye");
        user.setPassword("1994");
        user.setExtraInfo("{}");
        userMapper.insert(user);
    }
    
    @Test
    public void selectOne() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("username", "yee");
        User user = userMapper.selectOne(wrapper);
        log.info("user data: {}", JSON.toJSONString(user));
    }
}
