package com.dorohedoro.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderMsgDTO {

    private Long orderId;

    private Long userId;

    private Long goodsId;

    private Long shopId;

    private Long addressId;

    private Long deliverymanId;

    private Long settlementId;

    private Long rewardRecordId;

    private BigDecimal payAmount;

    private String status;

    private Boolean isConfirmed;
}
