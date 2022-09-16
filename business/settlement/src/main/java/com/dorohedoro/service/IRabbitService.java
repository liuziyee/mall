package com.dorohedoro.service;

import org.springframework.amqp.core.Message;

public interface IRabbitService {
    void handleMessage(Message message);
}
