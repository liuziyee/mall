package com.dorohedoro.service;

import com.dorohedoro.dto.OrderDTO;

public interface IOrderService {
    Long createOrder(OrderDTO orderDTO);
}
