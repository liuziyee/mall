package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Deliveryman;
import com.dorohedoro.enums.DeliverymanStatus;
import com.dorohedoro.mapper.DeliverymanMapper;
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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RabbitService implements IRabbitService {

    @Autowired
    private Channel channel;

    @Autowired
    private DeliverymanMapper deliverymanMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @RabbitListener(queues = "queue.delivery")
    public void handleMessage(@Payload Message message) {
        OrderMsgDTO orderMsgDTO = JSON.parseObject(message.getBody(), OrderMsgDTO.class);

        // 分配可用的骑手
        LambdaQueryWrapper<Deliveryman> wrapper = Wrappers.<Deliveryman>lambdaQuery()
                .eq(Deliveryman::getStatus, DeliverymanStatus.AVAILABLE);
        List<Deliveryman> deliverymen = deliverymanMapper.selectList(wrapper);
        int index = ThreadLocalRandom.current().nextInt(deliverymen.size());
        Deliveryman deliveryman = deliverymen.get(index);

        orderMsgDTO.setDeliverymanId(deliveryman.getId());

        rabbitTemplate.send(
                "exchange.order.delivery",
                "key.order",
                RabbitUtil.buildMessage(orderMsgDTO, null)
        );
    }
    
    public void rabbitApiDeclare() throws IOException {
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
