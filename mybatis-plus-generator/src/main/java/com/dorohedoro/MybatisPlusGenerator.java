package com.dorohedoro;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class MybatisPlusGenerator implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        FastAutoGenerator.create(
                "jdbc:mysql://127.0.0.1:3306/mall?autoReconnect=true&useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC",
                "root",
                "12345")
                .globalConfig(builder -> {
                    builder.author("liuziye")
                            .outputDir("C://Users//liuzi//Desktop//mybatis-plus");
                })
                .packageConfig(builder -> {
                    builder.parent("com.dorohedoro")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "C://Users//liuzi//Desktop//mybatis-plus//com//dorohedoro"));
                })
                .strategyConfig(builder -> builder.addInclude("deliveryman"))
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
