package com.dorohedoro.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import java.io.IOException;

public interface IRabbitService {
    void rabbitApiPublish(String exchange, String routingKey, Long ttl, byte[] payload);

    void rabbitTemplatePublish(String exchange, String routingKey, Long ttl, byte[] payload);

    void handleMessage(Message message, Channel channel) throws IOException;
}
