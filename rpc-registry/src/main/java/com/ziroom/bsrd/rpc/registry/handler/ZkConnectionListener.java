package com.ziroom.bsrd.rpc.registry.handler;

import com.ziroom.bsrd.log.ApplicationLogger;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk 连接事件处理
 *
 * @author chengys4
 *         2018-03-01 16:07
 **/
public class ZkConnectionListener implements ConnectionStateListener {

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        if (newState.equals(ConnectionState.CONNECTED)) {
            ApplicationLogger.info("zk connect success");
            //获取所有的服务
            Map<String, List<String>> serviceNodes = new HashMap<>();
            try {
                List<String> serviceList = client.getChildren().forPath("/");

                for (String service : serviceList) {
                    List<String> nodeList = client.getChildren().forPath("/" + service);
                    serviceNodes.put(service, nodeList);
                    RpcPathChildren rpcPathChildren = new RpcPathChildren(client, "/" + service);
                    try {
                        rpcPathChildren.start();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
