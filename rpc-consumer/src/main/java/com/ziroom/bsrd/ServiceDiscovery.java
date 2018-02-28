package com.ziroom.bsrd;

import com.ziroom.bsrd.log.ApplicationLogger;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ������
 */
public class ServiceDiscovery {

    public static String registryAddress = "10.16.37.112:3181";
    public static String namespace = "rpc";


    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);


    private volatile List<String> dataList = new ArrayList<>();

    private CuratorFramework curatorFramework;

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;
        curatorFramework = connectServer();
    }

    public String discover() {
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                LOGGER.debug("using only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
            }
        }
        return data;
    }

    private CuratorFramework connectServer() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString(registryAddress)
                        .sessionTimeoutMs(5000)
                        .connectionTimeoutMs(5000)
                        .retryPolicy(retryPolicy)
                        .namespace(namespace)
                        .build();
        client.start();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState.equals(ConnectionState.CONNECTED)) {
                    //获取所有的服务
                    Map<String, List<String>> serviceNodes = new HashMap<>();
                    try {
                        List<String> serviceList = client.getChildren().forPath("/");

                        for (String service : serviceList) {
                            List<String> nodeList = client.getChildren().forPath("/" + service);
                            ApplicationLogger.info("node:" + nodeList.toString());
                            serviceNodes.put(service, nodeList);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    ApplicationLogger.info("connect service ");
                    ConnectManage.getInstance().initServices(client, serviceNodes);
                }
            }
        });
        return client;
    }

    private void watchNode(final CuratorFramework curatorFramework) {
        try {
            String parentPath = "/rpc";
            PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, parentPath, true);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    System.out.println("事件类型：" + event.getType() + "；操作节点：" + event.getData().getPath());
                }
            });

//            updateConnectedServer();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
