package com.dorohedoro.service;

public interface IRabbitMQService {
    void publish(String exchange, String routingKey, Long ttl, byte[] payload);
}
