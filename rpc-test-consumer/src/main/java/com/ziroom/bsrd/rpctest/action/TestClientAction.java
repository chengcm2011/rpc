package com.ziroom.bsrd.rpctest.action;


import com.ziroom.bsrd.rpctest.IHelloService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TestClientAction {

    @Resource
    IHelloService helloService;

    @RequestMapping("/hello")
    public String helloService() throws Exception {
        String userName = "chengyingsheng";
        return helloService.hello(userName);
    }

}
