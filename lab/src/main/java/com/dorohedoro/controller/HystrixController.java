package com.dorohedoro.controller;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.service.hystrix.HystrixAnnotation;
import com.dorohedoro.service.hystrix.NacosHystrixCommand;
import com.dorohedoro.service.NacosService;
import com.dorohedoro.service.hystrix.cache.NacosUseCacheHystrixCommand;
import com.dorohedoro.service.hystrix.cache.UseCacheHystrixAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@RestController
@RequestMapping("/hystrix")
public class HystrixController {

    @Autowired
    private HystrixAnnotation hystrixAnnotation;

    @Autowired
    private NacosService nacosService;

    @Autowired
    private UseCacheHystrixAnnotation useCacheHystrixAnnotation;

    @GetMapping("/annotation/{id}")
    public List<ServiceInstance> getServiceInstanceByAnnotation(@PathVariable String id) {
        log.info("call nacos service by postman...");
        log.info("service id: {}, thread: {}", id, Thread.currentThread().getName());
        return hystrixAnnotation.getServiceInstance(id);
    }

    @GetMapping("/command/{id}")
    public List<ServiceInstance> getServiceInstanceByCommand(@PathVariable String id) throws ExecutionException, InterruptedException {
        List<ServiceInstance> res = new NacosHystrixCommand(nacosService, id).execute();
        log.info("use execute to call nacos service...");
        log.info("thread: {}, data: {}", Thread.currentThread().getName(), JSON.toJSONString(res));

        Future<List<ServiceInstance>> future = new NacosHystrixCommand(nacosService, id).queue();
        res = future.get();
        log.info("use queue to call nacos service...");
        log.info("thread: {}, data: {}", Thread.currentThread().getName(), JSON.toJSONString(res));


        Observable<List<ServiceInstance>> Observable = new NacosHystrixCommand(nacosService, id).observe();
        res = Observable.toBlocking().single();
        log.info("use observe to call nacos service...");
        log.info("thread: {}, data: {}", Thread.currentThread().getName(), JSON.toJSONString(res));

        rx.Observable<List<ServiceInstance>> toObservable = new NacosHystrixCommand(nacosService, id).toObservable();
        res = toObservable.toBlocking().single();
        log.info("use toObservable to call nacos service...");
        log.info("thread: {}, data: {}", Thread.currentThread().getName(), JSON.toJSONString(res));

        return res;
    }

    @GetMapping("/cache-command/{id}")
    public void getServiceInstanceByCommandUseCache(@PathVariable String id) throws Exception {
        NacosUseCacheHystrixCommand command1 = new NacosUseCacheHystrixCommand(nacosService, id);
        NacosUseCacheHystrixCommand command2 = new NacosUseCacheHystrixCommand(nacosService, id);

        command1.execute();
        command2.execute();

        NacosUseCacheHystrixCommand.flushCache(id);

        NacosUseCacheHystrixCommand command3 = new NacosUseCacheHystrixCommand(nacosService, id);
        NacosUseCacheHystrixCommand command4 = new NacosUseCacheHystrixCommand(nacosService, id);

        command3.execute();
        command4.execute();
    }

    @GetMapping("/cache-annotation/{id}")
    public void getServiceInstanceByAnnotationUseCache(@PathVariable String id) throws Exception {
        useCacheHystrixAnnotation.getServiceInstance(id);
        useCacheHystrixAnnotation.getServiceInstance(id);

        useCacheHystrixAnnotation.clearCache(id);

        useCacheHystrixAnnotation.getServiceInstance(id);
        useCacheHystrixAnnotation.getServiceInstance(id);
    }
    
}

    
    
