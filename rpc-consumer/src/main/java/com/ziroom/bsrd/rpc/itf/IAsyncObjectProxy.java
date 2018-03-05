package com.ziroom.bsrd.rpc.itf;

import com.ziroom.bsrd.rpc.netty.RPCFuture;

public interface IAsyncObjectProxy {
    RPCFuture call(String methodName, Object... args);
}