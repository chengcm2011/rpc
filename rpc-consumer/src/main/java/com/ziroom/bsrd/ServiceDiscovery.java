package com.ziroom.bsrd;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ServiceDiscovery {


    public static String namespace = "rpc";


    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CuratorFramework curatorFramework;


    public ServiceDiscovery() {
//        initServiceNode("10.16.37.112:3181");
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

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

                System.out.println(newState);
                if (newState.equals(ConnectionState.CONNECTED)) {
                    LOGGER.info("zk connect success");
                    //获取所有的服务
                    Map<String, List<String>> serviceNodes = new HashMap<>();
                    try {
                        List<String> serviceList = client.getChildren().forPath("/");

                        for (String service : serviceList) {
                            List<String> nodeList = client.getChildren().forPath("/" + service);
                            LOGGER.info("node:" + nodeList.toString());
                            serviceNodes.put(service, nodeList);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    LOGGER.info("connect service begin");
                    ConnectManage.getInstance().initServices(client, serviceNodes);
                    LOGGER.info("connect service end");
                }
            }
        });
//        watchNode(client);
        return client;
    }

    private void watchNode(final CuratorFramework curatorFramework) {
        try {
            String parentPath = "/";
            PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, parentPath, true);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    if (event != null) {
                        LOGGER.info("eventType:" + event.getType() + "-eventNode:" + event.getData().getPath());
                    } else {
                        LOGGER.error(" event is null");
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void stop() {
        curatorFramework.close();
    }
}
