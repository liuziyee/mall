package com.dorohedoro.util;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public class RabbitUtil {
    
    public static Message buildMessage(byte[] payload, Long ttl) {
        MessageProperties msgProps = new MessageProperties();
        if (ttl != null) {
            msgProps.setExpiration(ttl.toString());
        }
        return new Message(payload, msgProps);
    }
}
