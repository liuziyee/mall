package com.dorohedoro.bean;

import lombok.Data;

@Data
public class ResponseBean<T> {
    private Long code;
    private T data;
    private String msg;
}
