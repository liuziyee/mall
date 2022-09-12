package com.dorohedoro.service;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Settlement;
import com.dorohedoro.enums.SettlementStatus;
import com.dorohedoro.mapper.SettlementMapper;
import com.dorohedoro.util.IDGenerator;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMQService {

    @Autowired
    private Channel channel;

    @Autowired
    private SettlementMapper settlementMapper;

    @PostConstruct
    public void rabbitApiDeclare() throws IOException {
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
