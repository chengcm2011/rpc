package com.ziroom.bsrd.rpc.registry;

import com.ziroom.bsrd.basic.Predef;
import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.util.IpUtil;
import com.ziroom.bsrd.rpc.vo.NodeVO;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.Set;

/**
 * 服务注册
 *
 * @author chengys4
 *         2018-03-07 16:14
 **/
public class ServiceRegistry {

    private String registerAddress;
    private String namespace;

    public ServiceRegistry() {

    }

    public ServiceRegistry(String registerAddress, String namespace) {
        this.registerAddress = registerAddress;
        this.namespace = namespace;
    }

    public void registerServices(int port, Set<String> servicesItfList) throws Exception {

        if (port < 0 || Predef.size(servicesItfList) <= 0) {
            return;
        }

        String serverAddress = IpUtil.getLocalIp() + ":" + port;

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString(registerAddress)
                        .sessionTimeoutMs(5000)
                        .connectionTimeoutMs(5000)
                        .retryPolicy(retryPolicy)
                        .namespace(namespace)
                        .build();
        client.start();

        for (String interfaceName : servicesItfList) {
            String interfaceNamePath = "/" + interfaceName;
            Stat ifacePathStat = client.checkExists().forPath(interfaceNamePath);
            String nodeData = NodeVO.SERVICE + ";" + interfaceName;
            if (ifacePathStat == null) {
                client.create().withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(interfaceNamePath, nodeData.getBytes());
            } else {
                client.setData().forPath(interfaceNamePath, nodeData.getBytes());
            }
            String serverAddressPath = interfaceNamePath + "/" + serverAddress;
            Stat addreddStat = client.checkExists().forPath(serverAddressPath);
            if (addreddStat != null) {
                client.delete().forPath(serverAddressPath);
            }
            nodeData = NodeVO.NODE + ";" + interfaceName + ";" + serverAddress;
            client.create().withMode(CreateMode.EPHEMERAL).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(serverAddressPath, nodeData.getBytes());
            ApplicationLogger.info(" RegistryCentry reginster service on zk success interfaceName:{},serverAddress:{}, path:{}", interfaceName, serverAddress, serverAddressPath);
        }

    }
}
