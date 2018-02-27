package com.ziroom.bsrd;

public interface IAsyncObjectProxy {
    public RPCFuture call(String funcName, Object... args);
}