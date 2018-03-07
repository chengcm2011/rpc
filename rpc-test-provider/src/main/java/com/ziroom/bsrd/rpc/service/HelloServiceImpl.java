package com.ziroom.bsrd.rpc.service;


import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.annotation.RpcService;
import com.ziroom.bsrd.rpctest.IHelloService;
import com.ziroom.bsrd.rpctest.Person;

@RpcService(IHelloService.class)
public class HelloServiceImpl implements IHelloService {

    @Override
    public String hello(String name) {
        ApplicationLogger.info("receive request:" + name);
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        ApplicationLogger.info("receive request:" + person.toString());
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
