package com.ziroom.bsrd.rpctest.config;

import com.ziroom.bsrd.rpc.RpcClientProxy;
import com.ziroom.bsrd.rpctest.IHelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chengys4
 *         2018-03-06 19:17
 **/
@Configuration
public class ServiceConfig {

    @Bean
    public IHelloService helloService() throws Exception {
        RpcClientProxy rpcClientProxy = new RpcClientProxy(IHelloService.class);
        return (IHelloService) rpcClientProxy.getObject();
    }

}
