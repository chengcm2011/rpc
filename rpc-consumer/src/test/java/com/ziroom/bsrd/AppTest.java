package com.ziroom.bsrd;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppTest {

    private static String registryAddress = "101.200.228.102:2181";
    private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);


    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(10000, 3);
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString(registryAddress)
                        .sessionTimeoutMs(5000)
                        .connectionTimeoutMs(50000)
                        .retryPolicy(retryPolicy)
                        .namespace("rpc")
                        .build();
        client.start();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
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
        client.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
            @Override
            public void unhandledError(String message, Throwable e) {
                LOGGER.info("CuratorFramework unhandledError: {}", message);
            }
        });
    }
}
