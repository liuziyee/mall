package com.dorohedoro.unit;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class RabbitMQTest {
    
    @Test
    public void declare() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("110.40.136.113");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("liuziye1994");

        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(
                    "exchange.order.shop",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            channel.queueDeclare(
                    "queue.order",
                    true,
                    false,
                    false,
                    null
            );

            channel.queueBind(
                    "queue.order",
                    "exchange.order.shop",
                    "key.order",
                    null
            );

            channel.exchangeDeclare(
                    "exchange.order.delivery",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            channel.queueBind(
                    "queue.order",
                    "exchange.order.delivery",
                    "key.order",
                    null
            );
        } catch (Exception e) {
            log.info("{}", e.getMessage(), e);
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
    
}
