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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitMQService {

    @Autowired
    private Channel channel;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @PostConstruct
    public void init() throws IOException, TimeoutException {
        channel.exchangeDeclare(
                "exchange.order.shop",
                BuiltinExchangeType.DIRECT,
                true,
                false,
                null
        );

        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 10000); // 设置队列TTL

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

        //channel.basicQos(3);
        channel.basicConsume("queue.shop", false, callback, consumerTag -> {});
    }
}
