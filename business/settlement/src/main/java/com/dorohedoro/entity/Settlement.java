package com.dorohedoro.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.dorohedoro.enums.SettlementStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Settlement implements Serializable {

    @TableId
    private Long id;
    
    private Long orderId;
    
    private Long transactionId;
    
    private BigDecimal payAmount;
    
    private SettlementStatus status;
    
    @TableField(fill = FieldFill.INSERT, select = false)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE, select = false)
    private Date updateTime;
}
