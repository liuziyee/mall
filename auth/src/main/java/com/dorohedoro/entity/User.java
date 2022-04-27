package com.dorohedoro.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

//@Entity
//@EntityListeners(AuditingEntityListener.class)
//@Table(name = "user")
@Data
@TableName("user")
public class User implements Serializable {
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "id", nullable = false)
    //private Long id;
    //
    //@Column(name = "username", nullable = false)
    //private String username;
    //
    //@Column(name="password", nullable = false)
    //private String password;
    //
    //@Column(name = "extra_info", nullable = false)
    //private String extraInfo;
    //
    //@CreatedDate
    //@Column(name = "create_time", nullable = false)
    //private Date createTime;
    //
    //@LastModifiedDate
    //@Column(name = "update_time", nullable = false)
    //private Date updateTime;

    @TableId
    private Long id;

    private String username;
    
    private String password;

    private String extraInfo;

    @TableField(fill = FieldFill.INSERT, select = false)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, select = false)
    private Date updateTime;

    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
