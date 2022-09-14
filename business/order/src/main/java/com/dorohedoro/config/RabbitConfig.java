package com.dorohedoro.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfig {

    @Bean
    RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        rabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        return rabbitListenerContainerFactory;
    }
    
    /*@Bean
    public MessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory, Queue queue) {
        SimpleMessageListenerContainer msgListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        msgListenerContainer.addQueues(queue); // 设置监听队列
        msgListenerContainer.setConcurrentConsumers(3); // 设置消费者数
        msgListenerContainer.setMaxConcurrentConsumers(5);
        msgListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 手动签收

        MessageListenerAdapter msgListenerAdapter = new MessageListenerAdapter();
        msgListenerAdapter.setDelegate(rabbitMQService); // 指定代理对象
        
        Map<String, String> map = new HashMap<>();
        map.put(queue.getName(), "handleMessage"); // 指定监听队列和代理对象方法的映射关系
        msgListenerAdapter.setQueueOrTagToMethodName(map);

        Jackson2JsonMessageConverter msgConverter = new Jackson2JsonMessageConverter();
        msgConverter.setClassMapper(new ClassMapper() {
            @Override
            public void fromClass(Class<?> clazz, MessageProperties properties) {}

            @Override
            public Class<?> toClass(MessageProperties properties) {
                return OrderMsgDTO.class;
            }
        });
        msgListenerAdapter.setMessageConverter(msgConverter);

        msgListenerContainer.setMessageListener(msgListenerAdapter);
        return msgListenerContainer;
    }*/

    // 声明式配置
    @Bean
    public Exchange exchange01() {
        return new DirectExchange("exchange.order.shop");
    }

    @Bean
    public Exchange exchange02() {
        return new DirectExchange("exchange.order.delivery");
    }

    @Bean
    public Exchange exchange03() {
        return new FanoutExchange("exchange.order.to.settlement"); // 用来订单服务投递消息给结算服务
    }

    @Bean
    public Exchange exchange04() {
        return new FanoutExchange("exchange.settlement.to.order"); // 用来结算服务投递消息给订单服务
    }

    @Bean
    public Exchange exchange05() {
        return new TopicExchange("exchange.order.reward");
    }

    @Bean
    public Queue queue() {
        return new Queue("queue.order");
    }

    @Bean
    public Binding binding01(@Qualifier("exchange01") Exchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to((DirectExchange) exchange).with("key.order");
    }

    @Bean
    public Binding binding02(@Qualifier("exchange02") Exchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to((DirectExchange) exchange).with("key.order");
    }

    @Bean
    public Binding binding03(@Qualifier("exchange04") Exchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to((FanoutExchange) exchange);
    }

    @Bean
    public Binding binding04(@Qualifier("exchange05") Exchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to((TopicExchange) exchange).with("key.order");
    }
}


