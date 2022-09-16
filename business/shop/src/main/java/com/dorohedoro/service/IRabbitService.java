package com.dorohedoro.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import java.io.IOException;

public interface IRabbitService {
    void handleMessage(Message message, Channel channel) throws IOException;
}
