package com.dorohedoro.service;

public interface IRabbitMQService {
    void rabbitApiPublish(String exchange, String routingKey, Long ttl, byte[] payload);

    void rabbitTemplatePublish(String exchange, String routingKey, Long ttl, byte[] payload);

    void handleMessage(byte[] payload);
}
