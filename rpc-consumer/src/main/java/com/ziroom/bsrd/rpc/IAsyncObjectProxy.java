package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.rpc.netty.RPCFuture;

public interface IAsyncObjectProxy {
    RPCFuture call(String funcName, Object... args);
}