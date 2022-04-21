package com.dorohedoro.exception;

import com.dorohedoro.constant.ResCode;

public class BizException extends RuntimeException{
    private ResCode code;

    public BizException(ResCode code) {
        super(code.getDesc());
        this.code = code;
    }
    
    public Long getCode() {
        return this.code.getCode();
    }
    
    public String getMsg() {
        return this.code.getDesc();
    }
}
