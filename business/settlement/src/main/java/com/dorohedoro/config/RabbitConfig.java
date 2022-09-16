package com.dorohedoro.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    
    @Bean
    public Exchange exchange01() {
        return new FanoutExchange("exchange.order.to.settlement");
    }

    @Bean
    public Exchange exchange02() {
        return new FanoutExchange("exchange.settlement.to.order");
    }
    
    @Bean
    public Queue queue() {
        return new Queue("queue.settlement");
    }
    
    @Bean
    public Binding binding(@Qualifier("exchange01") Exchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to((FanoutExchange) exchange);
    }
}
