package com.dorohedoro.dto;

import com.dorohedoro.enums.OrderStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderDTO implements Serializable {

    private Long id;

    private Long userId;

    private Long goodsId;

    private Long shopId;

    private Long addressId;

    private Long deliverymanId;

    private Long settlementId;

    private Long rewardRecordId;

    private BigDecimal payAmount;

    private OrderStatus status;
}
