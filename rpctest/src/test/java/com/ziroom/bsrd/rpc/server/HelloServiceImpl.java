package com.ziroom.bsrd.rpc.server;


import com.ziroom.bsrd.client.IHelloService;
import com.ziroom.bsrd.client.Person;
import com.ziroom.bsrd.rpc.annotation.RpcService;

@RpcService(IHelloService.class)
public class HelloServiceImpl implements IHelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
