package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.rpc.registry.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chengys4
 *         2018-03-06 18:15
 **/
@Configuration
public class RpcProviderInit {

    @Autowired
    ServiceRegistry serviceRegistry;

    @Bean
    public RpcServer rpcServer() throws Exception {
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        return rpcServer;
    }
}
