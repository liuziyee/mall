package com.dorohedoro.advice;

import com.dorohedoro.dto.ResponseBean;
import com.dorohedoro.biz.BizException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionAdvice {
    @ExceptionHandler(BizException.class)
    public ResponseBean handleBizException(BizException e) {
        ResponseBean resBean = new ResponseBean();
        resBean.setCode(e.getCode());
        resBean.setMsg(e.getMsg());
        return resBean;
    }
}
