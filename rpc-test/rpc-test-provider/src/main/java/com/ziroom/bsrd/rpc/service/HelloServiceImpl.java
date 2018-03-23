package com.ziroom.bsrd.rpc.service;


import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.annotation.RpcService;
import com.ziroom.bsrd.rpctest.IHelloService;
import org.springframework.stereotype.Service;

@RpcService(IHelloService.class)
@Service
public class HelloServiceImpl implements IHelloService {

    @Override
    public String hello(String name) {
        ApplicationLogger.info("receive request:" + name);
        return "Hello! " + name;
    }
}
