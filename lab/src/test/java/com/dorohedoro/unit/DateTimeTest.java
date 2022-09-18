package com.dorohedoro.unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class DateTimeTest {
    
    @Test
    public void datetimeToStr() {
        log.info("{}", new StringBuilder("20220101")
                .insert(4, "-")
                .insert(7, "-")
                .append(" 00:00:00")
                .toString());

        log.info("{}", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
    }
    
    @Test
    public void strToDatetimeToMilli() {
        LocalDateTime datetime = LocalDateTime.parse(
                "2022-01-01 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("{}", datetime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }
    
    @Test
    public void minAndMax() {
        LocalDate date = LocalDate.parse("20220101", DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime min = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime max = LocalDateTime.of(date, LocalTime.MAX);
        log.info("{}", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(min));
        log.info("{}", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(max));
    }
}
