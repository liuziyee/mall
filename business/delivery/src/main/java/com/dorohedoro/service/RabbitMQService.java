package com.dorohedoro.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Deliveryman;
import com.dorohedoro.enums.DeliverymanStatus;
import com.dorohedoro.enums.GoodsStatus;
import com.dorohedoro.enums.ShopStatus;
import com.dorohedoro.mapper.DeliverymanMapper;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMQService {

    private Channel channel;

    @Autowired
    private DeliverymanMapper deliverymanMapper;
    
    @PostConstruct
    public void init() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("110.40.136.113");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root1994");
        
        channel = connectionFactory.newConnection().createChannel();

        channel.exchangeDeclare(
                "exchange.order.delivery",
                BuiltinExchangeType.DIRECT,
                true,
                false,
                null
        );

        channel.queueDeclare(
                "queue.delivery",
                true,
                false,
                false,
                null
        );

        channel.queueBind(
                "queue.delivery",
                "exchange.order.delivery",
                "key.delivery",
                null
        );
        
        DeliverCallback callback = (consumerTag, message) -> {
            String payload = new String(message.getBody());
            OrderMsgDTO orderMsgDTO = JSON.parseObject(payload, OrderMsgDTO.class);
            
            // 分配可用的骑手
            LambdaQueryWrapper<Deliveryman> wrapper = Wrappers.<Deliveryman>lambdaQuery()
                    .eq(Deliveryman::getStatus, DeliverymanStatus.AVAILABLE);
            List<Deliveryman> deliverymen = deliverymanMapper.selectList(wrapper);
            int index = ThreadLocalRandom.current().nextInt(deliverymen.size());
            Deliveryman deliveryman = deliverymen.get(index);
            
            orderMsgDTO.setDeliverymanId(deliveryman.getId());

            channel.basicPublish(
                    "exchange.order.delivery",
                    "key.order",
                    null,
                    JSON.toJSONString(orderMsgDTO).getBytes()
            );
        };

        channel.basicConsume("queue.delivery", true, callback, consumerTag -> {});
    }
}
