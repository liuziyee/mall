package com.dorohedoro.service;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Goods;
import com.dorohedoro.entity.Shop;
import com.dorohedoro.mapper.GoodsMapper;
import com.dorohedoro.mapper.ShopMapper;
import com.dorohedoro.enums.GoodsStatus;
import com.dorohedoro.enums.ShopStatus;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitMQService {

    private Channel channel;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private GoodsMapper goodsMapper;
    
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
                "queue.shop",
                true,
                false,
                false,
                null
        );

        channel.queueBind(
                "queue.shop",
                "exchange.order.shop",
                "key.shop",
                null
        );

        DeliverCallback callback = (consumerTag, message) -> {
            String payload = new String(message.getBody());
            OrderMsgDTO orderMsgDTO = JSON.parseObject(payload, OrderMsgDTO.class);

            Shop shop = shopMapper.selectById(orderMsgDTO.getShopId());
            Goods goods = goodsMapper.selectById(orderMsgDTO.getGoodsId());
            
            orderMsgDTO.setIsConfirmed(false);
            if (shop.getStatus().equals(ShopStatus.OPEN) && goods.getStatus().equals(GoodsStatus.AVAILABLE)) {
                orderMsgDTO.setPayAmount(goods.getPrice());
                orderMsgDTO.setIsConfirmed(true);
            }
            
            // 路由失败回调接口
            channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
                log.info("[ROUTE FAILED] code: {}, text: {}, exchange: {}, routingKey: {}, props: {}, body: {}",
                        replyCode, replyText, exchange, routingKey, properties, new String(body));
            });
            channel.basicPublish(
                    "exchange.order.shop",
                    "key.order",
                    true, // 将路由失败的消息返回给发送方
                    null,
                    JSON.toJSONString(orderMsgDTO).getBytes()
            );

            // 手动签收
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        
        channel.basicConsume("queue.shop", false, callback, consumerTag -> {});
    }
}
