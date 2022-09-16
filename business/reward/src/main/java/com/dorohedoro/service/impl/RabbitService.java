package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.RewardRecord;
import com.dorohedoro.enums.RewardChannel;
import com.dorohedoro.mapper.RewardRecordMapper;
import com.dorohedoro.service.IRabbitService;
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
    private RewardRecordMapper rewardRecordMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @RabbitListener(queues = "queue.reward")
    public void handleMessage(@Payload Message message) {
        OrderMsgDTO orderMsgDTO = JSON.parseObject(message.getBody(), OrderMsgDTO.class);

        // 生成积分记录
        RewardRecord rewardRecord = new RewardRecord();
        rewardRecord.setUserId(orderMsgDTO.getUserId());
        rewardRecord.setChannel(RewardChannel.ORDER);
        rewardRecord.setChannelId(orderMsgDTO.getOrderId());
        rewardRecord.setAmount(orderMsgDTO.getPayAmount());
        rewardRecordMapper.insert(rewardRecord);

        orderMsgDTO.setRewardRecordId(rewardRecord.getId());

        rabbitTemplate.send(
                "exchange.order.reward",
                "key.order",
                RabbitUtil.buildMessage(orderMsgDTO, null)
        );
    }

    public void rabbitApiDeclare() throws IOException {
        channel.exchangeDeclare(
                "exchange.order.reward",
                BuiltinExchangeType.TOPIC,
                true,
                false,
                null
        );

        channel.queueDeclare(
                "queue.reward",
                true,
                false,
                false,
                null
        );

        channel.queueBind(
                "queue.reward",
                "exchange.order.reward",
                "key.reward",
                null
        );

        DeliverCallback callback = (consumerTag, message) -> {
            String payload = new String(message.getBody());
            OrderMsgDTO orderMsgDTO = JSON.parseObject(payload, OrderMsgDTO.class);

            // 生成积分记录
            RewardRecord rewardRecord = new RewardRecord();
            rewardRecord.setUserId(orderMsgDTO.getUserId());
            rewardRecord.setChannel(RewardChannel.ORDER);
            rewardRecord.setChannelId(orderMsgDTO.getOrderId());
            rewardRecord.setAmount(orderMsgDTO.getPayAmount());
            rewardRecordMapper.insert(rewardRecord);

            orderMsgDTO.setRewardRecordId(rewardRecord.getId());

            channel.basicPublish(
                    "exchange.order.reward",
                    "key.order",
                    null,
                    JSON.toJSONString(orderMsgDTO).getBytes()
            );
        };

        channel.basicConsume("queue.reward", true, callback, consumerTag -> {
        });
    }
}
