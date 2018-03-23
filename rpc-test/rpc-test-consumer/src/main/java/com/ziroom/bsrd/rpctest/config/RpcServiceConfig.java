package com.ziroom.bsrd.rpctest.config;

import com.ziroom.bsrd.base.itf.IDataDict;
import com.ziroom.bsrd.rpc.config.RpcReferenceConfig;
import com.ziroom.bsrd.rpctest.IHelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chengys4
 *         2018-03-06 19:17
 **/
@Configuration
public class RpcServiceConfig {

    @Bean
    public IHelloService helloService() throws Exception {
        RpcReferenceConfig rpcClientProxy = new RpcReferenceConfig(IHelloService.class);
        return (IHelloService) rpcClientProxy.getObject();
    }

    @Bean
    public IDataDict dataDict() throws Exception {
        RpcReferenceConfig rpcClientProxy = new RpcReferenceConfig(IDataDict.class);
        return (IDataDict) rpcClientProxy.getObject();
    }

}
