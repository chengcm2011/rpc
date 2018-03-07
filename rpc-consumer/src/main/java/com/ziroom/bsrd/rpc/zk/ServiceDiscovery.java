package com.ziroom.bsrd.rpc.zk;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.InitializingBean;

public class ServiceDiscovery implements InitializingBean {

    private String namespace;

    private String registryAddress;

    private CuratorFramework curatorFramework;

    public ServiceDiscovery(String registryAddress, String namespace) {
        this.registryAddress = registryAddress;
        this.namespace = namespace;
    }

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public ServiceDiscovery() {

    }

    public void discover() {
        if (StringUtils.isBlank(registryAddress)) {
            throw new IllegalArgumentException("registryAddress is null ");
        }
        if (StringUtils.isBlank(registryAddress)) {
            namespace = "rpc";
        }
        curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(registryAddress)
                .retryPolicy(new RetryNTimes(2000, 20000)).namespace(namespace)
                .build();
        curatorFramework.start();
        curatorFramework.getConnectionStateListenable().addListener(new ZkConnectionListener());
        initChildrenChangeListener(curatorFramework);
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

    @Override
    public void afterPropertiesSet() throws Exception {
        discover();
    }
}
