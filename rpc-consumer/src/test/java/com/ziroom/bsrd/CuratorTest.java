package com.ziroom.bsrd;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CuratorTest {


    private static final String root = "/testroot";
    private static final String first = "/testroot/first";
    private static final String second = "/testroot/first/second";
    private static final Logger LOGGER = LoggerFactory.getLogger(CuratorTest.class);

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("10.16.37.112:3181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

//        client.create().withMode(CreateMode.PERSISTENT)
//                .forPath("/zk-huey", "hello".getBytes());
//        client.create().withMode(CreateMode.EPHEMERAL)
//                .forPath("/zk-huey/ddd", "hello".getBytes());

        /**
         * 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
         */
        ExecutorService pool = Executors.newFixedThreadPool(5);

        /**
         * 监听数据节点的变化情况
         */
//


        /**
         * 监听子节点的变化情况
         */
        final PathChildrenCache childrenCache = new PathChildrenCache(client, root, true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener(
                new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                            throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED:
                                LOGGER.info("CHILD_ADDED: " + event.getData().getPath());
                                break;
                            case CHILD_REMOVED:
                                LOGGER.info("CHILD_REMOVED: " + event.getData().getPath());
                                break;
                            case CHILD_UPDATED:
                                LOGGER.info("CHILD_UPDATED: " + event.getData().getPath() + "--" + new String(event.getData().getData()));
                                break;
                            default:
                                break;
                        }
                    }
                },
                pool
        );


        Stat f = client.checkExists().forPath(root);
        if (f == null) {
            client.create().withMode(CreateMode.PERSISTENT)
                    .forPath(root, "root".getBytes());
            Thread.sleep(5 * 1000);
        }
        Stat fd = client.checkExists().forPath(first);
        if (fd == null) {
            client.create().withMode(CreateMode.PERSISTENT)
                    .forPath(first, "first".getBytes());
            Thread.sleep(5 * 1000);
            final PathChildrenCache childrenCachess = new PathChildrenCache(client, first, true);
            childrenCachess.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            childrenCachess.getListenable().addListener(
                    new PathChildrenCacheListener() {
                        @Override
                        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                                throws Exception {
                            switch (event.getType()) {
                                case CHILD_ADDED:
                                    LOGGER.info("CHILD_ADDED: " + event.getData().getPath());
                                    break;
                                case CHILD_REMOVED:
                                    LOGGER.info("CHILD_REMOVED: " + event.getData().getPath());
                                    break;
                                case CHILD_UPDATED:
                                    LOGGER.info("CHILD_UPDATED: " + event.getData().getPath() + "--" + new String(event.getData().getData()));
                                    break;
                                default:
                                    break;
                            }
                        }
                    },
                    pool
            );
        }
        client.create().withMode(CreateMode.EPHEMERAL)
                .forPath(second, "second".getBytes());
        client.create().withMode(CreateMode.EPHEMERAL)
                .forPath(first + "/111", "111".getBytes());
        client.create().withMode(CreateMode.EPHEMERAL)
                .forPath(first + "/222", "222".getBytes());
        client.create().withMode(CreateMode.EPHEMERAL)
                .forPath(first + "/333", "333".getBytes());
        Thread.sleep(1 * 1000);

        client.setData().forPath(second, "secondData".getBytes());
        Thread.sleep(1 * 1000);
        client.setData().forPath(first, "firstData".getBytes());
        Thread.sleep(1 * 1000);
        client.delete()
                .forPath(second);
        Thread.sleep(1 * 1000);
        client.delete().forPath(first + "/111");
        client.delete().forPath(first + "/222");
        client.delete().forPath(first + "/333");
        client.delete()
                .forPath(first);
        Thread.sleep(1 * 1000);
        pool.shutdown();
        client.close();
    }
}