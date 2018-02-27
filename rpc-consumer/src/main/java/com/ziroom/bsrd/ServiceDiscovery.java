package com.ziroom.bsrd;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 服务发现
 */
public class ServiceDiscovery {

    public static String registryAddress = "10.16.37.112:3181";
    public static String namespace = "rpc";


    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private volatile List<String> dataList = new ArrayList<>();

    private CuratorFramework curatorFramework;

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;
        curatorFramework = connectServer();
        if (curatorFramework != null) {
            watchNode(curatorFramework);
        }
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
        return client;
    }

    private void watchNode(final CuratorFramework curatorFramework) {
        try {
//            List<String> nodeList = curatorFramework.getChildren().watched().forPath("/sdds");

            PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, "/rpc", true);

            PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    System.out.println("开始进行事件分析:-----");
                    ChildData data = event.getData();
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            System.out.println("CHILD_ADDED : " + data.getPath() + "  数据:" + data.getData());
                            break;
                        case CHILD_REMOVED:
                            System.out.println("CHILD_REMOVED : " + data.getPath() + "  数据:" + data.getData());
                            break;
                        case CHILD_UPDATED:
                            System.out.println("CHILD_UPDATED : " + data.getPath() + "  数据:" + data.getData());
                            break;
                        default:
                            break;
                    }
                }
            };
            childrenCache.getListenable().addListener(childrenCacheListener);
            System.out.println("Register zk watcher successfully!");
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);


//            List<String> dataList = new ArrayList<>();
//            for (String node : nodeList) {
//                byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
//                dataList.add(new String(bytes));
//            }
//            LOGGER.debug("node data: {}", dataList);
//            this.dataList = dataList;

            LOGGER.debug("Service discovery triggered updating connected com.ziroom.bsrd.server node.");
            updateConnectedServer();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private void updateConnectedServer() {
        ConnectManage.getInstance().updateConnectedServer(this.dataList);
    }

}
