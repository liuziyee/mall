package com.dorohedoro.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dorohedoro.enums.RewardChannel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("reward_record")
public class RewardRecord implements Serializable {
    
    @TableId
    private Long id;
    
    private Long userId;
    
    private BigDecimal amount;
    
    private RewardChannel channel;
    
    private Long channelId;
    
    @TableField(fill = FieldFill.INSERT, select = false)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, select = false)
    private Date updateTime;
}
