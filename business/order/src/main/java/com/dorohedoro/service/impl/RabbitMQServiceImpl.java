package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Order;
import com.dorohedoro.mapper.OrderMapper;
import com.dorohedoro.service.IRabbitMQService;
import com.dorohedoro.enums.OrderStatus;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitMQServiceImpl implements IRabbitMQService {

    private Channel channel;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Async("executor")
    public void publish(String exchange, String routingKey, byte[] payload) {
        try {
            channel.basicPublish(exchange, routingKey, null, payload);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    @PostConstruct
    public void init() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("110.40.136.113");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root1994");
        
        channel = connectionFactory.newConnection().createChannel();

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

        DeliverCallback callback = (consumerTag, message) -> {
            String payload = new String(message.getBody());
            OrderMsgDTO orderMsgDTO = JSON.parseObject(payload, OrderMsgDTO.class);

            Long orderId = orderMsgDTO.getOrderId();
            Order order = orderMapper.selectById(orderId);
            
            switch (order.getStatus()) {
                case CREATING:
                    if (orderMsgDTO.getIsConfirmed() && orderMsgDTO.getPayAmount() != null) {
                        // 更新订单状态和支付金额
                        order.setStatus(OrderStatus.SHOP_CONFIRMED);
                        order.setPayAmount(orderMsgDTO.getPayAmount());
                        orderMapper.update(order, Wrappers.<Order>lambdaQuery().eq(Order::getId, order.getId()));
                        
                        channel.basicPublish(
                                "exchange.order.delivery",
                                "key.delivery",
                                null,
                                JSON.toJSONString(orderMsgDTO).getBytes()
                        );
                        break;
                    }
                    order.setStatus(OrderStatus.FAILED);
                    break;
                case SHOP_CONFIRMED:
                    break;
                case DELIVERYMAN_CONFIRMED:
                    break;
                case SETTLEMENT_CONFIRMED:
                    break;
                case CREATED:
                    break;
                case FAILED:
                    break;
            }
        };

        // 监听队列
        channel.basicConsume("queue.order", true, callback, consumerTag -> {});
    }
}
