package com.ziroom.bsrd;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ServiceDiscovery {


    public static String namespace = "rpc";


    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CuratorFramework curatorFramework;


    public ServiceDiscovery() {

    }

    public String discover(String registryAddress) {

        curatorFramework = initServiceNode(registryAddress);

        return "ss";
    }

    private CuratorFramework initServiceNode(String registryAddress) {


        CuratorFramework client = CuratorFrameworkFactory

                .builder()

                .connectString(registryAddress)

                .retryPolicy(new RetryNTimes(2000, 20000)).namespace(namespace)

                .build();

        client.start();

        client.getConnectionStateListenable().addListener(new ZkConnectionListener());

        initChildrenChangeListener(client);
        return client;
    }

    private void initChildrenChangeListener(CuratorFramework client) {
        RpcPathChildren rpcPathChildren = new RpcPathChildren(client);
        try {
            rpcPathChildren.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        curatorFramework.close();
    }
}
