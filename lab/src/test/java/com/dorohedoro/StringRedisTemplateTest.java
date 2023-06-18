package com.dorohedoro;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class StringRedisTemplateTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void string() {
        stringRedisTemplate.opsForValue().set("day", "618", 1000, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().append("day", "...");

        log.info("has key {}: {}", "day", stringRedisTemplate.hasKey("day"));
        log.info("datatype: {}", stringRedisTemplate.type("day").code());
        log.info("expire: {}", stringRedisTemplate.getExpire("day"));
        log.info("day: {}", stringRedisTemplate.opsForValue().get("day"));
    }
    
    @Test
    public void list() {
        stringRedisTemplate.opsForList().leftPushAll("language", "java", "go", "c");
        log.info("languages: {}", stringRedisTemplate.opsForList().range("language", 0, -1));
        
    }
    
    @Test
    public void set() {
        stringRedisTemplate.opsForSet().add("city", "tokyo", "hongkong", "berlin");
        log.info("city: {}, size: {}", 
                stringRedisTemplate.opsForSet().members("city"),
                stringRedisTemplate.opsForSet().size("city"));
    }
    
    @Test
    public void zSet() {
        stringRedisTemplate.opsForZSet().add("city", "tokyo", 30);
        stringRedisTemplate.opsForZSet().add("city", "hongkong", 100);

        Set<ZSetOperations.TypedTuple<String>> citySet = stringRedisTemplate.opsForZSet().rangeByScoreWithScores("city", 0, 100);
        citySet.forEach(tuple -> {
            log.info("{}: {}", tuple.getValue(), tuple.getScore());
        });
    }
    
    @Test
    public void hash() {
        Map<String, Object> map = new HashMap<>();
        map.put("100", 1);
        map.put("101", 3);
        
        stringRedisTemplate.opsForHash().putAll("nissin:117", map);
        Set keys = stringRedisTemplate.opsForHash().keys("nissin:117");
        log.info("keys: {}, values: {}", keys, stringRedisTemplate.opsForHash().multiGet("nissin:117", keys));
    }
}
