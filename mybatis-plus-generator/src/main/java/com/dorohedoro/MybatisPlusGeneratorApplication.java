package com.dorohedoro;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Collections;

@Slf4j
@SpringBootApplication
public class MybatisPlusGeneratorApplication implements ApplicationListener<ContextRefreshedEvent> {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusGeneratorApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        FastAutoGenerator.create(
                "jdbc:mysql://127.0.0.1:3306/mall?aotuReconnect=true&useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC",
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
                .strategyConfig(builder -> builder.addInclude("user"))
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
