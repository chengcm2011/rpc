package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.rpc.registry.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author chengys4
 *         2018-03-06 18:15
 **/
@Configuration
public class RpcProviderInit {
    @Autowired
    Environment env;

    @Autowired
    ServiceRegistry serviceRegistry;

    @Bean
    public RpcServer rpcServer() throws Exception {
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.setPort(Integer.valueOf(env.getProperty("rpc.server.port", "8087")));
        return rpcServer;
    }
}
