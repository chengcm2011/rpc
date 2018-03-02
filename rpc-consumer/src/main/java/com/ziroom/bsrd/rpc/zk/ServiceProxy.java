package com.ziroom.bsrd.rpc.zk;

import com.ziroom.bsrd.rpc.IAsyncObjectProxy;
import com.ziroom.bsrd.rpc.netty.ConnectManage;
import com.ziroom.bsrd.rpc.netty.RPCFuture;
import com.ziroom.bsrd.rpc.netty.RpcClientHandler;
import com.ziroom.bsrd.rpc.vo.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author chengys4
 *         2018-02-27 17:52
 **/
public class ServiceProxy<T> implements InvocationHandler, IAsyncObjectProxy {

    private Class<T> clazz;

    public ServiceProxy(Class<T> interfaceClass) {
        this.clazz = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        RpcClientHandler handler = ConnectManage.getInstance().chooseHandler(clazz.getName());
        RPCFuture rpcFuture = handler.sendRequest(request);
        return rpcFuture.get();

    }

    @Override
    public RPCFuture call(String methodName, Object... args) {
        RpcClientHandler handler = ConnectManage.getInstance().chooseHandler(this.clazz.getName());
        RpcRequest request = createRequest(this.clazz.getName(), methodName, args);
        RPCFuture rpcFuture = handler.sendRequest(request);
        return rpcFuture;
    }

    private RpcRequest createRequest(String className, String methodName, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);

        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);

        return request;
    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }

        return classType;
    }
}
