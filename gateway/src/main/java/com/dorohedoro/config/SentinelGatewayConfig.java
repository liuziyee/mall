package com.dorohedoro.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.constant.ResCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Configuration
public class SentinelGatewayConfig {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public SentinelGatewayConfig(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                 ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void doInit() {
        //log.info("load gateway flow rules by hard code...");
        //initCustomizeApis();
        //initGatewayRules();

        initBlockHandler();
    }

    //private void initCustomizeApis() {
    //    Set<ApiDefinition> definitions = new HashSet<>();
    //    ApiDefinition api1 = new ApiDefinition("lab-nacos-api")
    //            .setPredicateItems(new HashSet<ApiPredicateItem>() {{
    //                add(new ApiPathPredicateItem()
    //                        .setPattern("/gateway/lab/nacos/**")
    //                        .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
    //            }});
    //    definitions.add(api1);
    //
    //    GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    //}
    
    //public void initGatewayRules() {
    //    Set<GatewayFlowRule> rules = new HashSet<>();
    //
    //    rules.add(new GatewayFlowRule("lab-nacos-api")
    //            .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
    //            .setCount(1)
    //            .setIntervalSec(1));
    //
    //    GatewayRuleManager.loadRules(rules);
    //}

    private void initBlockHandler() {
        GatewayCallbackManager.setBlockHandler((serverWebExchange, throwable) -> {
            log.warn("blocked by sentinel and the resource is : {}", 
                    ((BlockException)throwable).getRule().getResource());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("code", ResCode.blocked_by_sentinel.getCode().toString());
            bodyMap.put("msg", ResCode.blocked_by_sentinel.getDesc());

            return ServerResponse
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(bodyMap));
        });
    }
}
