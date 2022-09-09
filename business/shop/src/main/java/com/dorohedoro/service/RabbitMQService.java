package com.dorohedoro.service;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Goods;
import com.dorohedoro.entity.Shop;
import com.dorohedoro.mapper.GoodsMapper;
import com.dorohedoro.mapper.ShopMapper;
import com.dorohedoro.enums.GoodsStatus;
import com.dorohedoro.enums.ShopStatus;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMQService {

    private Channel channel;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private GoodsMapper goodsMapper;
    
    @PostConstruct
    public void init() throws IOException, TimeoutException {
        // 商家服务做为消费者要做的
        // 声明交换机和要监听的队列
        // 绑定队列到交换机
        // 实现回调接口
        // 监听队列
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

            channel.basicPublish(
                    "exchange.order.shop",
                    "key.order",
                    null,
                    JSON.toJSONString(orderMsgDTO).getBytes()
            );
        };
        
        channel.basicConsume("queue.shop", true, callback, consumerTag -> {});
    }
}
