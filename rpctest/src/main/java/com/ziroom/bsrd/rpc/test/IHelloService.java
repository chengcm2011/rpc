package com.ziroom.bsrd.rpc.test;

public interface IHelloService {
    String hello(String name);

    String hello(Person person);
}
