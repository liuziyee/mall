package com.dorohedoro.filter;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Slf4j
@WebFilter(
        urlPatterns = "/*",
        asyncSupported = true
)
public class HystrixRequestCotextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HystrixRequestContext.initializeContext();

        configHystrixConcurrencyStrategy();
        
        filterChain.doFilter(servletRequest, servletResponse);
        
    }

    public void configHystrixConcurrencyStrategy() {
            HystrixConcurrencyStrategy strategy = HystrixPlugins.getInstance().getConcurrencyStrategy();
            
            if (strategy instanceof HystrixConcurrencyStrategyDefault) {
                return;
            }

            HystrixCommandExecutionHook commandExecutionHook = 
                    HystrixPlugins.getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier = 
                    HystrixPlugins.getInstance().getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = 
                    HystrixPlugins.getInstance().getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = 
                    HystrixPlugins.getInstance().getPropertiesStrategy();

            HystrixPlugins.reset();
            
            HystrixPlugins.getInstance().registerConcurrencyStrategy(
                    HystrixConcurrencyStrategyDefault.getInstance()
            );
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
    }
    
}
