package com.dorohedoro.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    
    @Bean
    public NewTopic ok() {
        return TopicBuilder.name("ok")
                .partitions(5)
                .replicas(1)
                .build();
    }
}
