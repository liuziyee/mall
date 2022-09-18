package com.dorohedoro.service.hystrix.merge;

import com.dorohedoro.service.NacosService;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import org.springframework.cloud.client.ServiceInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class HystrixCollapserImpl extends HystrixCollapser<List<List<ServiceInstance>>, List<ServiceInstance>, String> {

    private final NacosService nacosService;
    
    private final String serviceId;

    public HystrixCollapserImpl(NacosService nacosService, String serviceId) {
        super(
                HystrixCollapser.Setter.withCollapserKey(
                        HystrixCollapserKey.Factory.asKey("GetServiceInstanceBatchCommand")
                ).andCollapserPropertiesDefaults(
                        HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(300)
                )
        );
        this.nacosService = nacosService;
        this.serviceId = serviceId;
    }

    @Override
    public String getRequestArgument() {
        return this.serviceId;
    }

    @Override
    protected HystrixCommand<List<List<ServiceInstance>>> createCommand(Collection<CollapsedRequest<List<ServiceInstance>, String>> collapsedRequests) {
        List<String> serviceIds = new ArrayList<>(collapsedRequests.size());
        serviceIds.addAll(
                collapsedRequests.stream().
                        map(CollapsedRequest::getArgument).
                        collect(Collectors.toList()));
        return new UseMergeHystrixCommandImpl(nacosService, serviceIds);
    }

    @Override
    protected void mapResponseToRequests(List<List<ServiceInstance>> batchResponses, Collection<CollapsedRequest<List<ServiceInstance>, String>> collapsedRequests) {
        int index = 0;
        for (CollapsedRequest<List<ServiceInstance>, String> collapsedRequest : collapsedRequests) {
            List<ServiceInstance> serviceInstances = batchResponses.get(index++);
            collapsedRequest.setResponse(serviceInstances);
        }
    }
}
