package com.dorohedoro.service;

import org.springframework.amqp.core.Message;

public interface IRabbitService {
    void rabbitApiPublish(String exchange, String routingKey, Long ttl, byte[] payload);

    void rabbitTemplatePublish(String exchange, String routingKey, Long ttl, byte[] payload);

    void handleMessage(Message message);
}
