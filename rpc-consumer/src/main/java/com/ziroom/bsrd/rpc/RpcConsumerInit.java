package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.rpc.registry.ServiceDiscovery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author chengys4
 *         2018-03-06 19:17
 **/
@Configuration
public class RpcConsumerInit {

    @Autowired
    Environment env;

    @Bean
    public ServiceDiscovery serviceDiscovery() {
        String serverLists = env.getProperty("zookeeper.serverLists");
        if (StringUtils.isBlank(serverLists)) {

        }
        String namespace = env.getProperty("zookeeper.rpcnamespace");
        if (StringUtils.isBlank(namespace)) {
            namespace = "rpc";
        }
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        serviceDiscovery.setNamespace(namespace);
        serviceDiscovery.setRegistryAddress(serverLists);
        return serviceDiscovery;
    }

}
