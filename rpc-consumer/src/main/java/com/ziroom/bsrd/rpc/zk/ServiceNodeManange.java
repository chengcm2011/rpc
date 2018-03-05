package com.ziroom.bsrd.rpc.zk;


import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.itf.NodeType;
import com.ziroom.bsrd.rpc.vo.NodeVO;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务器节点管理
 *
 * @author chengys4
 *         2018-03-05 11:08
 **/
public class ServiceNodeManange {

    private static Map<String, List<NodeVO>> serviceNodes = new ConcurrentHashMap<>();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private AtomicInteger roundRobin = new AtomicInteger(0);
    private volatile static ServiceNodeManange serviceNodeManange;

    private ServiceNodeManange() {
    }

    public static ServiceNodeManange getInstance() {
        if (serviceNodeManange == null) {
            synchronized (ServiceNodeManange.class) {
                if (serviceNodeManange == null) {
                    serviceNodeManange = new ServiceNodeManange();
                }
            }
        }
        return serviceNodeManange;
    }

    /**
     * 选择服务器
     *
     * @param serviceName
     * @return
     */
    public NodeVO chooseHandler(String serviceName) {
        List<NodeVO> handlers = serviceNodes.get(serviceName);
        int size = handlers.size();
        int index = (roundRobin.getAndAdd(1) + size) % size;
        NodeVO selectNode = handlers.get(index);
        ApplicationLogger.info("service={} select={}", serviceName, selectNode.getNodeStr());
        return selectNode;
    }


    public void updateServicesNode(String data) {

    }

    public void removeServicesNode(String data) {
        NodeVO nodeVo = getNode(data);
        if (nodeVo.getType().equals(NodeType.NODE)) {
            removeServerNode(nodeVo);
        }
    }

    private void removeServerNode(NodeVO nodeVo) {

        List<NodeVO> nodeVOList = serviceNodes.get(nodeVo.getServiceName());
        if (nodeVOList == null) {
            nodeVOList = new ArrayList<>();
        }
        int removeIndex = 0;
        for (int i = 0; i < nodeVOList.size(); i++) {
            NodeVO clientHandler = nodeVOList.get(i);
            if (clientHandler.getNode().equals(nodeVo.getNodeStr())) {
                removeIndex = i;
            }
        }
        if (removeIndex != 0) {
            nodeVOList.remove(removeIndex);
        }
        serviceNodes.put(nodeVo.getServiceName(), nodeVOList);

    }

    public void addServicesNode(String data) {
        NodeVO nodeVo = getNode(data);
        if (nodeVo.getType().equals(NodeType.NODE)) {
            addServerNode(nodeVo);
        }
    }

    private void addServerNode(NodeVO nodeVo) {
        List<NodeVO> nodeVOList = serviceNodes.get(nodeVo.getServiceName());
        if (nodeVOList == null) {
            nodeVOList = new ArrayList<>();
        }
        nodeVOList.add(nodeVo);
        serviceNodes.put(nodeVo.getServiceName(), nodeVOList);
    }

    private NodeVO getNode(String data) {
        String[] d = data.split(";");
        NodeVO nodeVo = new NodeVO(d[0]);
        if (NodeType.SERVICE.equals(nodeVo.getType())) {
            nodeVo.setServiceName(d[1]);
        }
        if (NodeType.NODE.equals(nodeVo.getType())) {
            nodeVo.setServiceName(d[1]);
            nodeVo.setNode(parseNode(d[2]));
            nodeVo.setNodeStr(d[2]);
        }
        return nodeVo;
    }

    /**
     * 解析服务器地址
     *
     * @param node
     * @return
     */
    private InetSocketAddress parseNode(String node) {
        String[] array = node.split(":");
        if (array.length == 2) {
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            final InetSocketAddress remotePeer = new InetSocketAddress(host, port);
            return remotePeer;
        }
        ApplicationLogger.info("node error ");
        return null;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }
}
