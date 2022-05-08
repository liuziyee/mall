package com.dorohedoro.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseBean<T> implements Serializable {
    private Long code;
    private T data;
    private String msg;
}
