package com.dorohedoro.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;

public interface IRabbitService {
    void handleMessage(@Payload Message message, Channel channel) throws IOException;
}
