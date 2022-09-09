package com.dorohedoro.service.impl;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.dto.OrderDTO;
import com.dorohedoro.dto.OrderMsgDTO;
import com.dorohedoro.entity.Order;
import com.dorohedoro.mapper.OrderMapper;
import com.dorohedoro.service.IOrderService;
import com.dorohedoro.service.IRabbitMQService;
import com.dorohedoro.util.BeanUtil;
import com.dorohedoro.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IRabbitMQService rabbitMQService;

    @Override
    public Long createOrder(OrderDTO orderDTO) {
        Order order = BeanUtil.copy(orderDTO, Order.class);
        order.setStatus(OrderStatus.CREATING);
        orderMapper.insert(order);

        OrderMsgDTO orderMsgDTO = BeanUtil.copy(order, OrderMsgDTO.class);
        orderMsgDTO.setOrderId(order.getId());

        rabbitMQService.publish(
                "exchange.order.shop", 
                "key.shop", 
                JSON.toJSONString(orderMsgDTO).getBytes());

        return order.getId();
    }
}
