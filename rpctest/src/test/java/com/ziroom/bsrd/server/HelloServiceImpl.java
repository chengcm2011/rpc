package com.ziroom.bsrd.server;


import com.ziroom.bsrd.annotation.RpcService;
import com.ziroom.bsrd.client.IHelloService;
import com.ziroom.bsrd.client.Person;

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
