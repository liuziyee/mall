package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Order;
import com.dorohedoro.enums.OrderStatus;
import com.dorohedoro.mapper.OrderMapper;
import com.dorohedoro.service.IRabbitService;
import com.dorohedoro.util.IDGenerator;
import com.dorohedoro.util.RabbitUtil;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class RabbitServiceImpl implements IRabbitService {

    @Autowired
    private Channel channel;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Async("pool")
    public void rabbitApiPublish(String exchange, String routingKey, Long ttl, byte[] payload) {
        try {
            channel.confirmSelect(); // 置为确认模式

            AMQP.BasicProperties props = null;
            if (ttl != null) {
                props = new AMQP.BasicProperties
                        .Builder()
                        .expiration(String.valueOf(ttl)) // 设置消息TTL
                        .build();
            }

            channel.basicPublish(exchange, routingKey, props, payload);

            channel.waitForConfirms(); // 同步确认
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Async("pool")
    public void rabbitTemplatePublish(String exchange, String routingKey, Long ttl, byte[] payload) {
        Message msg = RabbitUtil.buildMessage(payload, ttl);
        CorrelationData corrData = new CorrelationData();
        corrData.setId(IDGenerator.nextId().toString());
        rabbitTemplate.send(exchange, routingKey, msg, corrData);
    }

    @Override
    @RabbitListener(queues = "queue.order")
    public void handleMessage(@Payload Message message, Channel channel) throws IOException {
        byte[] payload = message.getBody();
        OrderMsgDTO orderMsgDTO = JSON.parseObject(payload, OrderMsgDTO.class);
        Long orderId = orderMsgDTO.getOrderId();
        Order order = orderMapper.selectById(orderId);

        CorrelationData corrData = new CorrelationData();
        corrData.setId(IDGenerator.nextId().toString());

        switch (order.getStatus()) {
            case CREATING:
                // 商家服务投递的消息
                if (orderMsgDTO.getIsConfirmed() && orderMsgDTO.getPayAmount() != null) {
                    // 更新订单状态和支付金额
                    order.setStatus(OrderStatus.SHOP_CONFIRMED);
                    order.setPayAmount(orderMsgDTO.getPayAmount());
                    // 投递消息给骑手服务
                    rabbitTemplate.send(
                            "exchange.order.delivery",
                            "key.delivery",
                            RabbitUtil.buildMessage(payload, null),
                            corrData
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
                    rabbitTemplate.send(
                            "exchange.order.to.settlement",
                            "nothing",
                            RabbitUtil.buildMessage(payload, null),
                            corrData
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
                    rabbitTemplate.send(
                            "exchange.order.reward",
                            "key.reward",
                            RabbitUtil.buildMessage(payload, null),
                            corrData
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
        
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 手动签收
    }

    public void rabbitApiDeclare() throws IOException {
        // 订单服务做为消费者要做的
        // 声明交换机和要监听的队列
        // 绑定队列到交换机
        // 实现监听队列回调接口
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

        // 用于订单服务投递消息给结算服务
        channel.exchangeDeclare(
                "exchange.order.to.settlement",
                BuiltinExchangeType.FANOUT,
                true,
                false,
                null
        );

        // 用于结算服务投递消息给订单服务
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
            byte[] payload = message.getBody();
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
                                payload
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
                                payload
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
                                payload
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

        // 异步确认
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) {
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) {
            }
        });

        // 监听队列
        channel.basicConsume("queue.order", true, callback, consumerTag -> {
        });
    }
}
