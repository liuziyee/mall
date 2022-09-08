package com.dorohedoro.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dorohedoro.util.OrderStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("`order`")
public class Order implements Serializable {

    @TableId
    private Long id;
    
    private Long userId;
    
    private Long goodsId;
    
    private Long addressId;
    
    private Long deliverymanId;

    private Long settlementId;
    
    private Long rewardRecordId;
    
    private BigDecimal payAmount;
    
    private OrderStatus status;

    @TableField(fill = FieldFill.INSERT, select = false)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, select = false)
    private Date updateTime;
}
