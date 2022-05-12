package com.dorohedoro.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.exception.BizException;
import com.dorohedoro.handler.CommonBlockHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class FlowRuleController {
    
    @PostConstruct
    public void init() {
        List<FlowRule> flowRuleList = new ArrayList<>();

        FlowRule flowRule = new FlowRule();
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        flowRule.setCount(1);
        flowRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        flowRule.setResource("flowRule");
        flowRuleList.add(flowRule);

        FlowRuleManager.loadRules(flowRuleList);
    }
    
    @GetMapping("/flow-rule")
    @SentinelResource(
            value = "flowRule", 
            blockHandlerClass = CommonBlockHandler.class,
            blockHandler = "handleBlockException"
    )
    public String flowRuleByCode() {
        return "alibaba";
    }
    
}
