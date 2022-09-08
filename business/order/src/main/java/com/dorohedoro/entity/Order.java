package com.dorohedoro.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Order implements Serializable {

    @TableId
    private Long id;
    
    private Long uid;
    
    private Long goodsId;
    
    private Long addressId;
    
    private Long deliverymanId;

    private Long settlementId;
    
    private Long rewardRecordId;
    
    private BigDecimal payAmount;
    
    private String status;

    @TableField(fill = FieldFill.INSERT, select = false)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, select = false)
    private Date updateTime;
}
