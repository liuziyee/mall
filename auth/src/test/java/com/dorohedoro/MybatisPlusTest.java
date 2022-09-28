package com.dorohedoro;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MybatisPlusTest {
    @Autowired
    private UserMapper userMapper;
    
    @Test
    public void createRecord() {
        User user = new User();
        user.setUsername("liuyee");
        user.setPassword("1994");
        user.setExtraInfo("{}");
        userMapper.insert(user);
        log.info("user data: {}", JSON.toJSONString(user));
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
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, "liuziye"));
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
        //Page<User> page = new Page<>(1, 10);
        //
        //userMapper.selectPage(page, null);
        //log.info("page data: {}", JSON.toJSONString(page));

        Page page = new Page(1, 10);

        userMapper.selectPageByExtraInfo(page, "qq");
        log.info("page data: {}", JSON.toJSONString(page));
    }
    
    @Transactional
    @Test
    public void lock() throws Exception {
        userMapper.writeLock();
        TimeUnit.MINUTES.sleep(10);

        User user = new User();
        user.setUsername("jiaozi");
        user.setPassword("jiaozi1994");
        user.setExtraInfo("{}");
        userMapper.insert(user);
        log.info("user data: {}", JSON.toJSONString(user));

        userMapper.unlock();
    }
}
