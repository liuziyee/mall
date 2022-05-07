package com.dorohedoro.advice;

import com.dorohedoro.annotation.IgnoreResponseData;
import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.exception.BizException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionAdvice {
    @IgnoreResponseData
    @ExceptionHandler(BizException.class)
    public ResponseBean handleBizException(BizException e) {
        ResponseBean resBean = new ResponseBean();
        resBean.setCode(e.getCode());
        resBean.setMsg(e.getMsg());
        return resBean;
    }
}
