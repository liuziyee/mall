package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Goods;
import com.dorohedoro.entity.Shop;
import com.dorohedoro.enums.GoodsStatus;
import com.dorohedoro.enums.ShopStatus;
import com.dorohedoro.mapper.GoodsMapper;
import com.dorohedoro.mapper.ShopMapper;
import com.dorohedoro.service.IRabbitService;
import com.dorohedoro.util.RabbitUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RabbitServiceImpl implements IRabbitService {

    @Autowired
    private Channel channel;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @RabbitListener(queues = "queue.shop")
    public void handleMessage(@Payload Message message, Channel channel) throws IOException {
        OrderMsgDTO orderMsgDTO = JSON.parseObject(message.getBody(), OrderMsgDTO.class);

        Shop shop = shopMapper.selectById(orderMsgDTO.getShopId());
        Goods goods = goodsMapper.selectById(orderMsgDTO.getGoodsId());

        orderMsgDTO.setIsConfirmed(false);
        if (shop.getStatus().equals(ShopStatus.OPEN) && goods.getStatus().equals(GoodsStatus.AVAILABLE)) {
            orderMsgDTO.setPayAmount(goods.getPrice());
            orderMsgDTO.setIsConfirmed(true);
        }

        rabbitTemplate.send(
                "exchange.order.shop",
                "key.order",
                RabbitUtil.buildMessage(orderMsgDTO, null),
                null);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    public void rabbitApiDeclare() throws IOException {
        // 配置死信队列要做的
        // 声明死信交换机和死信队列
        // 绑定
        // 服务监听的队列设置死信交换机
        channel.exchangeDeclare(
                "exchange.dlx",
                BuiltinExchangeType.TOPIC,
                true,
                false,
                null
        );

        channel.queueDeclare(
                "queue.dlx",
                true,
                false,
                false,
                null
        );

        channel.queueBind(
                "queue.dlx",
                "exchange.dlx",
                "#", // 通配，任意死信都会路由到该死信队列
                null
        );

        channel.exchangeDeclare(
                "exchange.order.shop",
                BuiltinExchangeType.DIRECT,
                true,
                false,
                null
        );

        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 60000); // 设置队列TTL
        args.put("x-max-length", 5); // 设置最大队列长度
        args.put("x-dead-letter-exchange", "exchange.dlx"); // 设置死信交换机

        channel.queueDeclare(
                "queue.shop",
                true,
                false,
                false,
                args
        );

        channel.queueBind(
                "queue.shop",
                "exchange.order.shop",
                "key.shop",
                null
        );

        DeliverCallback callback = (consumerTag, message) -> {
            Long deliveryTag = message.getEnvelope().getDeliveryTag();
            try {
                String payload = new String(message.getBody());
                OrderMsgDTO orderMsgDTO = JSON.parseObject(payload, OrderMsgDTO.class);

                Shop shop = shopMapper.selectById(orderMsgDTO.getShopId());
                Goods goods = goodsMapper.selectById(orderMsgDTO.getGoodsId());

                orderMsgDTO.setIsConfirmed(false);
                if (shop.getStatus().equals(ShopStatus.OPEN) && goods.getStatus().equals(GoodsStatus.AVAILABLE)) {
                    orderMsgDTO.setPayAmount(goods.getPrice());
                    orderMsgDTO.setIsConfirmed(true);
                }

                // 消息返回回调接口
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
                channel.basicAck(deliveryTag, false);

                // 一次签收多条
                //if (Math.floorMod(deliveryTag, 5) == 0) {
                //    channel.basicAck(deliveryTag, true);
                //}
            } catch (Exception e) {
                // 重回队列
                channel.basicNack(deliveryTag, false, true);
            }
        };

        channel.basicQos(3);
        channel.basicConsume("queue.shop", false, callback, consumerTag -> {
        });
    }
}
