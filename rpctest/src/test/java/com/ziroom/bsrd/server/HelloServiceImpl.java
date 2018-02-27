package com.ziroom.bsrd.server;


import com.ziroom.bsrd.annotation.RpcService;
import com.ziroom.bsrd.client.HelloService;
import com.ziroom.bsrd.client.Person;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
