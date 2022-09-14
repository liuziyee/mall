package com.dorohedoro.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {
    
    @Bean
    public Exchange exchange01() {
        return new DirectExchange("exchange.order.shop");
    }
    
    @Bean 
    public Exchange exchange02() {
        return new TopicExchange("exchange.dlx");
    }
    
    @Bean
    public Queue queue01() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 60000);
        args.put("x-max-length", 5);
        args.put("x-dead-letter-exchange", "exchange.dlx");
        return new Queue("queue.shop", true, false, false, args);
    }
    
    @Bean
    public Queue queue02() {
        return new Queue("queue.dlx");
    }
    
    @Bean
    public Binding binding01(@Qualifier("exchange01") Exchange exchange, @Qualifier("queue01") Queue queue) {
        return BindingBuilder.bind(queue).to((DirectExchange) exchange).with("key.shop");
    }

    @Bean
    public Binding binding02(@Qualifier("exchange02") Exchange exchange, @Qualifier("queue02") Queue queue) {
        return BindingBuilder.bind(queue).to((TopicExchange) exchange).with("#");
    }
}
