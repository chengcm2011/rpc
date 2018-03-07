package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.rpc.client.NettyClient;
import com.ziroom.bsrd.rpc.client.NettyClientPool;
import com.ziroom.bsrd.rpc.netty.RPCFuture;
import com.ziroom.bsrd.rpc.vo.NodeVO;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import com.ziroom.bsrd.rpc.zk.ServiceNodeManange;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc客户端
 *
 * @author chengys4
 *         2018-02-27 17:50
 **/
public class RpcClientProxy<T> implements FactoryBean<T>, InitializingBean {

    public static ConcurrentHashMap<String, RPCFuture> pendingRPC = new ConcurrentHashMap<>();

    private Class<T> itf;

    public RpcClientProxy(Class<T> itf) {
        this.itf = itf;
    }

    public Class<T> getItf() {
        return itf;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread()
                        .getContextClassLoader(), new Class[]{itf},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        // request
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setCreateMillisTime(System.currentTimeMillis());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        // send
                        return send(request);
                    }
                });
    }

    @Override
    public Class<T> getObjectType() {
        return itf;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }


    public Object send(RpcRequest request) throws Exception {


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
