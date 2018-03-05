package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.rpc.client.NettyClient;
import com.ziroom.bsrd.rpc.client.NettyClientPool;
import com.ziroom.bsrd.rpc.itf.IAsyncObjectProxy;
import com.ziroom.bsrd.rpc.netty.RPCFuture;
import com.ziroom.bsrd.rpc.vo.NodeVO;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import com.ziroom.bsrd.rpc.zk.ServiceDiscovery;
import com.ziroom.bsrd.rpc.zk.ServiceNodeManange;
import com.ziroom.bsrd.rpc.zk.ServiceProxy;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * rpc客户端
 *
 * @author chengys4
 *         2018-02-27 17:50
 **/
public class RpcClient implements InitializingBean {

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    private ServiceDiscovery serviceDiscovery;

    public static ConcurrentHashMap<String, RPCFuture> pendingRPC = new ConcurrentHashMap<>();

    public RpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public static <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ServiceProxy<T>(interfaceClass)
        );
    }

    public static <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ServiceProxy<>(interfaceClass);
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public void stop() {
        threadPoolExecutor.shutdown();
        serviceDiscovery.stop();
        ServiceNodeManange.getInstance().getEventLoopGroup().shutdownGracefully();
        NettyClientPool.stop();
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serviceDiscovery.discover();
    }

    public void init() throws Exception {
        afterPropertiesSet();
    }

    public static Object send(RpcRequest request) throws Exception {
        NodeVO nodeVO = ServiceNodeManange.getInstance().chooseHandler(request.getClassName());
        GenericObjectPool<NettyClient> clientPool = NettyClientPool.getNettyClientPool(nodeVO);

        // client proxt
        NettyClient clientPoolProxy = null;
        try {
            RPCFuture rpcFuture = new RPCFuture(request);
            pendingRPC.put(request.getRequestId(), rpcFuture);
            // rpc invoke
            clientPoolProxy = clientPool.borrowObject();

            clientPoolProxy.send(request);
            // future get
            return rpcFuture.get();
        } catch (Exception e) {
            throw e;
        } finally {
            clientPool.returnObject(clientPoolProxy);
        }

    }
}
