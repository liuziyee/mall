package com.dorohedoro.unit;

import com.dorohedoro.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

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
        BoundValueOperations stringOps = redisTemplate.boundValueOps("john");
        stringOps.set(user);

        log.info("john: {}", stringOps.get());
    }
    
    @Test
    public void hash() {
        BoundHashOperations hashOps = redisTemplate.boundHashOps("cart:117");
        hashOps.put("100", 1);
        hashOps.put("101", 3);

        log.info("entry: {}", hashOps.entries());
    }
}
