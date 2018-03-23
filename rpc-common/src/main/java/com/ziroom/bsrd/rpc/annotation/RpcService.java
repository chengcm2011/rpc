package com.ziroom.bsrd.rpc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcService {

    Class<?> value();

}