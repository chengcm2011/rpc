package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.rpc.annotation.RpcService;
import com.ziroom.bsrd.rpc.decoder.HessianSerializer;
import com.ziroom.bsrd.rpc.itf.IServer;
import com.ziroom.bsrd.rpc.netty.NettyServer;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import com.ziroom.bsrd.rpc.vo.RpcResponse;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * cheng
 * 2018-02-16
 */
public class RpcServer implements ApplicationContextAware, InitializingBean, DisposableBean {

    private static Map<String, Object> servicesMap = new HashMap<>();
    private static ThreadPoolExecutor threadPoolExecutor;
    private IServer server;
    private int port = 7080;


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                servicesMap.put(interfaceName, serviceBean);
            }
        }
    }


    public static RpcResponse invokeService(RpcRequest request, Object serviceBean) {
        if (serviceBean == null) {
            serviceBean = servicesMap.get(request.getClassName());
        }
        if (serviceBean == null) {
        }

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        try {
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            FastClass serviceFastClass = FastClass.create(serviceClass);
            FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);

            Object result = serviceFastMethod.invoke(serviceBean, parameters);

            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setError(t);
        }

        return response;
    }


    public void destroy() throws Exception {

    }

    public void afterPropertiesSet() throws Exception {
        server = NettyServer.class.newInstance();
        server.start(port, new HessianSerializer());

        RegistryCentry.registerServices(port, servicesMap.keySet());
    }


    public static void submit(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (RpcServer.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

}
