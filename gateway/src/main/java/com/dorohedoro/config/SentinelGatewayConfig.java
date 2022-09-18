package com.dorohedoro.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.dorohedoro.constant.ResCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
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

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //@Bean
    //@Order(-1)
    //public GlobalFilter sentinelGatewayFilter() {
    //    return new SentinelGatewayFilter();
    //}

    @PostConstruct
    public void doInit() {
        //log.info("config gateway flow rules by api...");
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
