package com.dorohedoro;

import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.util.IDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void string() {
        UserDTO user = new UserDTO();
        user.setId(117L);
        BoundValueOperations stringOps = redisTemplate.boundValueOps("nissin");
        stringOps.set(user);

        log.info("nissin: {}", stringOps.get());
    }

    @Test
    public void hash() {
        BoundHashOperations hashOps = redisTemplate.boundHashOps("nissin:117");
        hashOps.put("100", 1);
        hashOps.put("101", 3);

        log.info("entry: {}", hashOps.entries());
    }

    @Test
    public void hasKey() {
        if (redisTemplate.hasKey("token")) {
            redisTemplate.delete("token");
        }
        redisTemplate.opsForValue().set("token", IDGenerator.nextId(), 3, TimeUnit.DAYS);
    }

    @Test
    public void hashGet() {
        List<UserDTO> obj = (List<UserDTO>) redisTemplate.opsForHash().get("nissin", "117");
        Assert.assertTrue(obj == null);
    }
}
