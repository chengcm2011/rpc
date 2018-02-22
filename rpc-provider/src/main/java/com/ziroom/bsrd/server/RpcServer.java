package com.ziroom.bsrd.server;

import com.ziroom.bsrd.annotation.RpcService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * cheng
 * 2018-02-16
 */
public class RpcServer implements ApplicationContextAware, InitializingBean, DisposableBean {

    private Map<String, Object> handlerMap = new HashMap<>();

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    public void destroy() throws Exception {

    }

    public void afterPropertiesSet() throws Exception {

    }
}
