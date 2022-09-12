package com.dorohedoro.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfig {

    @Autowired
    public void init() {
        log.info("RabbitConfig init()...");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("110.40.136.113");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root1994");
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        
        // 声明交换机和要监听的队列
        // 绑定队列到交换机
        Exchange exchange = new DirectExchange("exchange.order.shop");
        rabbitAdmin.declareExchange(exchange);

        Queue queue = new Queue("queue.order");
        rabbitAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue).to((DirectExchange) exchange).with("key.order");
        rabbitAdmin.declareBinding(binding);

        exchange = new DirectExchange("exchange.order.delivery");
        rabbitAdmin.declareExchange(exchange);

        binding = BindingBuilder.bind(queue).to((DirectExchange) exchange).with("key.order");
        rabbitAdmin.declareBinding(binding);

        exchange = new FanoutExchange("exchange.order.to.settlement"); // 用来订单服务投递消息给结算服务
        rabbitAdmin.declareExchange(exchange);

        exchange = new FanoutExchange("exchange.settlement.to.order"); // 用来结算服务投递消息给订单服务
        rabbitAdmin.declareExchange(exchange);

        binding = BindingBuilder.bind(queue).to((FanoutExchange) exchange);
        rabbitAdmin.declareBinding(binding);

        exchange = new TopicExchange("exchange.order.reward");
        rabbitAdmin.declareExchange(exchange);

        binding = BindingBuilder.bind(queue).to((TopicExchange) exchange).with("key.order");
        rabbitAdmin.declareBinding(binding);
    }
}


