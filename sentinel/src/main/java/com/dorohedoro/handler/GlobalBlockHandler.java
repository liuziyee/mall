package com.dorohedoro.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.exception.BizException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalBlockHandler {

    public static String byAPIBlockHandler(BlockException e) {
        log.warn("blocked by sentinel and the rule is : {}", e.getRule());
        throw new BizException(ResCode.blocked_by_sentinel);
    }

    public static ResponseBean byDashboardBlockHandler(String id, BlockException e) {
        log.warn("blocked by sentinel and the rule is : {}", e.getRule());
        throw new BizException(ResCode.blocked_by_sentinel);
    }
}
