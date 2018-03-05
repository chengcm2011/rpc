package com.ziroom.bsrd.rpc.client;

import com.ziroom.bsrd.rpc.vo.NodeVO;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyClientPool {

    private GenericObjectPool<NettyClient> pool;

    public NettyClientPool(InetSocketAddress inetSocketAddress) {
        pool = new GenericObjectPool<>(new NettyClientFactory(inetSocketAddress));
        pool.setTestOnBorrow(true);
        pool.setMaxTotal(5);
    }

    private GenericObjectPool<NettyClient> getPool() {
        return this.pool;
    }

    private static ConcurrentHashMap<String, NettyClientPool> clientPoolMap = new ConcurrentHashMap<String, NettyClientPool>();

    public static GenericObjectPool<NettyClient> getNettyClientPool(NodeVO nodeVO)
            throws Exception {

        NettyClientPool clientPool = clientPoolMap.get(nodeVO.getNodeStr());
        if (clientPool != null) {
            return clientPool.getPool();
        }

        clientPool = new NettyClientPool(nodeVO.getNode());
        clientPoolMap.put(nodeVO.getNodeStr(), clientPool);
        return clientPool.getPool();
    }

    public static void stop() {
        for (Map.Entry<String, NettyClientPool> listEntry : clientPoolMap.entrySet()) {
            NettyClientPool rpcClientHandlerList = listEntry.getValue();
            rpcClientHandlerList.getPool().close();
        }
    }
}
