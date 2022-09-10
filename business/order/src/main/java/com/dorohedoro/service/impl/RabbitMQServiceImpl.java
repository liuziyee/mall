package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.stream.IntStream;

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
            channel.confirmSelect(); // 置为确认模式
            channel.basicPublish(exchange, routingKey, null, payload);
            // 同步确认
            //channel.waitForConfirms();
            // 异步确认
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) {
                    log.info("[ACK] deliveryTag: {}, isMultipleACK(是否为批量确认): {}", deliveryTag, multiple);
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) {
                    log.info("[NACK] deliveryTag: {}, isMultipleNACK(是否为批量确认): {}", deliveryTag, multiple);
                }
            });
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
        // 订单服务做为消费者要做的
        // 声明交换机和要监听的队列
        // 绑定队列到交换机
        // 实现回调接口
        // 监听队列
        channel.exchangeDeclare(
                "exchange.order.shop",
                BuiltinExchangeType.DIRECT,
                true,
                false,
                null
        );

        channel.exchangeDeclare(
                "exchange.order.delivery",
                BuiltinExchangeType.DIRECT,
                true,
                false,
                null
        );

        // 用来订单服务投递消息给结算服务
        channel.exchangeDeclare(
                "exchange.order.to.settlement",
                BuiltinExchangeType.FANOUT,
                true,
                false,
                null
        );

        // 用来结算服务投递消息给订单服务
        channel.exchangeDeclare(
                "exchange.settlement.to.order",
                BuiltinExchangeType.FANOUT,
                true,
                false,
                null
        );

        channel.exchangeDeclare(
                "exchange.order.reward",
                BuiltinExchangeType.TOPIC,
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

        channel.queueBind(
                "queue.order",
                "exchange.order.delivery",
                "key.order",
                null
        );

        channel.queueBind(
                "queue.order",
                "exchange.settlement.to.order",
                "nothing",
                null
        );

        channel.queueBind(
                "queue.order",
                "exchange.order.reward",
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
                    // 商家服务投递的消息
                    if (orderMsgDTO.getIsConfirmed() && orderMsgDTO.getPayAmount() != null) {
                        // 更新订单状态和支付金额
                        order.setStatus(OrderStatus.SHOP_CONFIRMED);
                        order.setPayAmount(orderMsgDTO.getPayAmount());
                        // 投递消息给骑手服务
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
                    // 骑手服务投递的消息
                    if (orderMsgDTO.getDeliverymanId() != null) {
                        // 更新订单状态和骑手ID
                        order.setStatus(OrderStatus.DELIVERYMAN_CONFIRMED);
                        order.setDeliverymanId(orderMsgDTO.getDeliverymanId());
                        LambdaQueryWrapper<Order> wrapper = Wrappers.<Order>lambdaQuery().eq(Order::getId, order.getId());
                        orderMapper.update(order, wrapper);
                        // 投递消息给结算服务
                        channel.basicPublish(
                                "exchange.order.to.settlement",
                                "nothing",
                                null,
                                JSON.toJSONString(orderMsgDTO).getBytes()
                        );
                        break;
                    }
                    order.setStatus(OrderStatus.FAILED);
                    break;
                case DELIVERYMAN_CONFIRMED:
                    // 结算服务投递的消息
                    if (orderMsgDTO.getSettlementId() != null) {
                        // 更新订单状态和结算ID
                        order.setStatus(OrderStatus.SETTLEMENT_CONFIRMED);
                        order.setSettlementId(orderMsgDTO.getSettlementId());
                        // 投递消息给积分服务
                        channel.basicPublish(
                                "exchange.order.reward",
                                "key.reward",
                                null,
                                JSON.toJSONString(orderMsgDTO).getBytes()
                        );
                        break;
                    }
                    order.setStatus(OrderStatus.FAILED);
                    break;
                case SETTLEMENT_CONFIRMED:
                    // 积分服务投递的消息
                    if (orderMsgDTO.getRewardRecordId() != null) {
                        // 更新订单状态和积分记录ID
                        order.setStatus(OrderStatus.CREATED);
                        order.setRewardRecordId(orderMsgDTO.getRewardRecordId());
                        break;
                    }
                    order.setStatus(OrderStatus.FAILED);
                    break;
            }
            // 订单数据落库
            LambdaQueryWrapper<Order> wrapper = Wrappers.<Order>lambdaQuery().eq(Order::getId, order.getId());
            orderMapper.update(order, wrapper);
        };

        // 监听队列
        channel.basicConsume("queue.order", true, callback, consumerTag -> {
        });
    }
}
