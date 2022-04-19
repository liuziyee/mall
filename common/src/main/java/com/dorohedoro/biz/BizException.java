package com.dorohedoro.biz;

import com.dorohedoro.util.ResCode;

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
