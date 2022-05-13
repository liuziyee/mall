package com.dorohedoro.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ResponseBean<T> implements Serializable {
    private Long code;
    private T data;
    private String msg;
}
