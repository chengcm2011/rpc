package com.ziroom.bsrd.rpc.config;

import com.ziroom.bsrd.rpc.registry.manage.ServiceNodeManage;
import com.ziroom.bsrd.rpc.transport.NettyClient;
import com.ziroom.bsrd.rpc.transport.NettyClientPool;
import com.ziroom.bsrd.rpc.transport.RPCFuture;
import com.ziroom.bsrd.rpc.vo.NodeVO;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengys4
 *         2018-03-07 15:58
 **/
public class RpcReferenceConfig implements FactoryBean<Object>, InitializingBean {
    public static ConcurrentHashMap<String, RPCFuture> pendingRPC = new ConcurrentHashMap<>();

    private Class<?> itf;

    public RpcReferenceConfig(Class<?> itf) {
        this.itf = itf;
    }

    public Class<?> getItf() {
        return itf;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(Thread.currentThread()
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
    public Class<?> getObjectType() {
        return itf;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }


    public Object send(RpcRequest request) throws Exception {


        NodeVO nodeVO = ServiceNodeManage.getInstance().chooseHandler(request.getClassName());
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
