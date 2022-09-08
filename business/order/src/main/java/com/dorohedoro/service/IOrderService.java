package com.dorohedoro.service;

import com.dorohedoro.dto.OrderDTO;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IOrderService {
    Long createOrder(OrderDTO orderDTO);
}
