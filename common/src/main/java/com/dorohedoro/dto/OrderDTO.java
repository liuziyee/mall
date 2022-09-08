package com.dorohedoro.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDTO implements Serializable {

    private Long id;

    private Long uid;

    private Long goodsId;

    private Long addressId;

    private Long deliverymanId;

    private Long settlementId;

    private Long rewardRecordId;

    private BigDecimal payAmount;

    private String status;

    private Date createTime;

    private Date updateTime;
}
