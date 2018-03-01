package com.ziroom.bsrd.rpc.vo;

import java.net.InetSocketAddress;

/**
 * @author chengys4
 *         2018-03-01 16:46
 **/
public class NodeVO {
    private String type;
    private String serviceName;
    private String nodeStr;
    private InetSocketAddress node;

    public NodeVO(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getNodeStr() {
        return nodeStr;
    }

    public void setNodeStr(String nodeStr) {
        this.nodeStr = nodeStr;
    }

    public InetSocketAddress getNode() {
        return node;
    }

    public void setNode(InetSocketAddress node) {
        this.node = node;
    }
}
