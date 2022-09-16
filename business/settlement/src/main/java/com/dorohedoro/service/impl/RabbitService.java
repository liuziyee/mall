package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Settlement;
import com.dorohedoro.enums.SettlementStatus;
import com.dorohedoro.mapper.SettlementMapper;
import com.dorohedoro.service.IRabbitService;
import com.dorohedoro.util.IDGenerator;
import com.dorohedoro.util.RabbitUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RabbitService implements IRabbitService {

    @Autowired
    private Channel channel;

    @Autowired
    private SettlementMapper settlementMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @RabbitListener(queues = "queue.settlement")
    public void handleMessage(@Payload Message message, Channel channel) throws IOException {
        OrderMsgDTO orderMsgDTO = JSON.parseObject(message.getBody(), OrderMsgDTO.class);
        
        // 生成结算记录
        Settlement settlement = new Settlement();
        settlement.setOrderId(orderMsgDTO.getOrderId());
        settlement.setTransactionId(IDGenerator.nextId());
        settlement.setPayAmount(orderMsgDTO.getPayAmount());
        settlement.setStatus(SettlementStatus.DONE);
        settlementMapper.insert(settlement);

        orderMsgDTO.setSettlementId(settlement.getId());

        rabbitTemplate.send(
                "exchange.settlement.to.order",
                "nothing",
                RabbitUtil.buildMessage(orderMsgDTO, null)
        );

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    public void rabbitApiDeclare() throws IOException {
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

        channel.queueDeclare(
                "queue.settlement",
                true,
                false,
                false,
                null
        );

        channel.queueBind(
                "queue.settlement",
                "exchange.order.to.settlement",
                "nothing",
                null
        );

        DeliverCallback callback = (consumerTag, message) -> {
            String payload = new String(message.getBody());
            OrderMsgDTO orderMsgDTO = JSON.parseObject(payload, OrderMsgDTO.class);
            // 生成结算记录
            Settlement settlement = new Settlement();
            settlement.setOrderId(orderMsgDTO.getOrderId());
            settlement.setTransactionId(IDGenerator.nextId());
            settlement.setPayAmount(orderMsgDTO.getPayAmount());
            settlement.setStatus(SettlementStatus.DONE);
            settlementMapper.insert(settlement);

            orderMsgDTO.setSettlementId(settlement.getId());

            channel.basicPublish(
                    "exchange.settlement.to.order",
                    "nothing",
                    null,
                    JSON.toJSONString(orderMsgDTO).getBytes()
            );
        };

        channel.basicConsume("queue.settlement", true, callback, consumerTag -> {
        });
    }
}
