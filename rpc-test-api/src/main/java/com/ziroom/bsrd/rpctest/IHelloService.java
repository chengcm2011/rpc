package com.ziroom.bsrd.rpctest;

public interface IHelloService {
    String hello(String name);

    String hello(Person person);
}
