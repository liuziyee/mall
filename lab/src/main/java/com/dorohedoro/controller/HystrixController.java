package com.dorohedoro.controller;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.bean.ResponseBean;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.service.NacosService;
import com.dorohedoro.service.hystrix.AuthHystrixCommand;
import com.dorohedoro.service.hystrix.HystrixAnnotation;
import com.dorohedoro.service.hystrix.HystrixCommandImpl;
import com.dorohedoro.service.hystrix.cache.UseCacheHystrixAnnotation;
import com.dorohedoro.service.hystrix.cache.UseCacheHystrixCommandImpl;
import com.dorohedoro.service.hystrix.merge.HystrixCollapserImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.*;
import rx.Observable;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private AuthHystrixCommand authHystrixCommand;

    @GetMapping("/annotation/{id}")
    public List<ServiceInstance> getServiceInstanceByAnnotation(@PathVariable String id) {
        return hystrixAnnotation.getServiceInstance(id);
    }

    @GetMapping("/command/{id}")
    public List<ServiceInstance> getServiceInstanceByCommand(@PathVariable String id) throws ExecutionException, InterruptedException {
        List<ServiceInstance> res = new HystrixCommandImpl(nacosService, id).execute();
        log.info("use execute to call nacos service...");
        log.info("thread: {}, data: {}", Thread.currentThread().getName(), JSON.toJSONString(res));

        Future<List<ServiceInstance>> future = new HystrixCommandImpl(nacosService, id).queue();
        res = future.get();
        log.info("use queue to call nacos service...");
        log.info("thread: {}, data: {}", Thread.currentThread().getName(), JSON.toJSONString(res));


        Observable<List<ServiceInstance>> Observable = new HystrixCommandImpl(nacosService, id).observe();
        res = Observable.toBlocking().single();
        log.info("use observe to call nacos service...");
        log.info("thread: {}, data: {}", Thread.currentThread().getName(), JSON.toJSONString(res));

        rx.Observable<List<ServiceInstance>> toObservable = new HystrixCommandImpl(nacosService, id).toObservable();
        res = toObservable.toBlocking().single();
        log.info("use toObservable to call nacos service...");
        log.info("thread: {}, data: {}", Thread.currentThread().getName(), JSON.toJSONString(res));

        return res;
    }

    @GetMapping("/cache-command/{id}")
    public void getServiceInstanceByCommandUseCache(@PathVariable String id) throws Exception {
        UseCacheHystrixCommandImpl command1 = new UseCacheHystrixCommandImpl(nacosService, id);
        UseCacheHystrixCommandImpl command2 = new UseCacheHystrixCommandImpl(nacosService, id);

        command1.execute();
        command2.execute();

        UseCacheHystrixCommandImpl.flushCache(id);

        UseCacheHystrixCommandImpl command3 = new UseCacheHystrixCommandImpl(nacosService, id);
        UseCacheHystrixCommandImpl command4 = new UseCacheHystrixCommandImpl(nacosService, id);

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

    @GetMapping("/merge-command/{id}")
    public void getServiceInstanceByCommandUseMerge(@PathVariable String id) throws Exception {
        new HystrixCollapserImpl(nacosService, id + 1).queue();
        new HystrixCollapserImpl(nacosService, id + 2).queue();
        new HystrixCollapserImpl(nacosService, id + 3).queue();

        TimeUnit.SECONDS.sleep(2);

        new HystrixCollapserImpl(nacosService, id + 4).queue();
    }

    @GetMapping("/merge-annotation/{id}")
    public void getServiceByAnnotationUseMerge(@PathVariable String id) throws Exception {
        nacosService.getService(id + 1);
        nacosService.getService(id + 2);
        nacosService.getService(id + 3);

        TimeUnit.SECONDS.sleep(2);

        nacosService.getService(id + 4);
    }

    @PostMapping("/feign/auth/login")
    public ResponseBean login(@RequestBody UserDTO userDTO) {
        return authHystrixCommand.login(userDTO);
    }
}

    
    
