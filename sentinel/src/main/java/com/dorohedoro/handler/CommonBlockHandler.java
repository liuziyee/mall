package com.dorohedoro.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.exception.BizException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonBlockHandler {

    public static String handleBlockException(BlockException e) {
        log.warn("block exception happened and the rule is : {}", e.getRule());
        throw new BizException(ResCode.sentinel_block);
    }
}
