package com.dorohedoro.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Exchange exchange() {
        return new DirectExchange("exchange.order.delivery");
    }

    @Bean
    public Queue queue() {
        return new Queue("queue.delivery");
    }
    
    @Bean
    public Binding binding(Exchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to((DirectExchange) exchange).with("key.delivery");
    }
}
