package com.dorohedoro;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class RabbitMQTest {

    @Test
    public void createExchangeAndQueue() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("110.40.136.113");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root1994");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            
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
            log.error(e.getMessage(), e);
        }
    }
}
