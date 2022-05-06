package com.dorohedoro.unit;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorohedoro.entity.User;
import com.dorohedoro.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        String username = "yee";
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(!StringUtils.isBlank(username), "username", username)
                .and(i -> i.isNotNull("extra_info").or().isNotNull("password"));
        User user = userMapper.selectOne(wrapper);
        log.info("user data: {}", JSON.toJSONString(user));
    }
    
    @Test
    public void deleteOne() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", "liuziyee");
        userMapper.delete(wrapper);
    }
    
    @Test
    public void updateOne() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", "liuziyee");

        User user = new User();
        user.setUsername("liuye");
        userMapper.update(user, wrapper);
    }
    
    @Test
    public void selectOneLambda() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "liuziye");

        User user = userMapper.selectOne(wrapper);
        log.info("user data: {}", JSON.toJSONString(user));
    }
    
    @Test
    public void updateOneLambda() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getUsername, "liuye");
        wrapper.set(User::getUsername, "liuziyee");

        userMapper.update(null, wrapper);
    }
    
    @Test
    public void selectPage() {
        Page<User> page = new Page<>(1, 10);

        userMapper.selectPage(page, null);
        log.info("page data: {}", JSON.toJSONString(page));
    }
    
}
