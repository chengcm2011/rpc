package com.ziroom.bsrd;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

/**
 *
 */
public class ServiceDiscovery {

    private String namespace;

    private String registryAddress;

    private CuratorFramework curatorFramework;

    public ServiceDiscovery() {

    }

    public ServiceDiscovery(String namespace, String registryAddress) {
        this.namespace = namespace;
        this.registryAddress = registryAddress;
    }

    public String discover() {

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

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void stop() {
        curatorFramework.close();
    }
}
