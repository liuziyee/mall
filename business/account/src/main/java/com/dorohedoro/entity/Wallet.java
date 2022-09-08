package com.dorohedoro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Wallet {

    @TableId(type = IdType.INPUT)
    private Long userId;
    
    private Long balance;
    
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
