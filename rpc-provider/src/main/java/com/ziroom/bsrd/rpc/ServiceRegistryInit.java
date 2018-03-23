package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.rpc.registry.ServiceRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author chengys4
 *         2018-03-07 17:21
 **/
@Configuration
public class ServiceRegistryInit {

    @Autowired
    Environment env;

    @Bean
    public ServiceRegistry serviceRegistry() {
        String serverLists = env.getProperty("rpc.zookeeper.serverLists");
        if (StringUtils.isBlank(serverLists)) {
            throw new IllegalArgumentException("rpc.zookeeper.serverLists not find ");
        }
        String rpcnamespace = env.getProperty("rpc.zookeeper.rpcnamespace");
        if (StringUtils.isBlank(rpcnamespace)) {
            rpcnamespace = "rpc";
        }
        return new ServiceRegistry(serverLists, rpcnamespace);
    }

}
