package com.dorohedoro.service;

import com.dorohedoro.dto.OrderMsgDTO;

public interface IRabbitService {
    void handleMessage(OrderMsgDTO orderMsgDTO);
}
