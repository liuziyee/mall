package com.dorohedoro.service;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.RewardRecord;
import com.dorohedoro.enums.RewardChannel;
import com.dorohedoro.mapper.RewardRecordMapper;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMQService {

    private Channel channel;

    @Autowired
    private RewardRecordMapper rewardRecordMapper;
    
    @PostConstruct
    public void init() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("110.40.136.113");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root1994");
        
        channel = connectionFactory.newConnection().createChannel();

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
        
        channel.basicConsume("queue.reward", true, callback, consumerTag -> {});
    }
}
