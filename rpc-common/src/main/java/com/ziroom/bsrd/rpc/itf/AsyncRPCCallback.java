package com.ziroom.bsrd.rpc.itf;

public interface AsyncRPCCallback {

    void success(Object result);

    void fail(Exception e);

}
