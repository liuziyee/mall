package com.dorohedoro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Address {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String receiverName;
    
    private String phone;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String detail;

    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
