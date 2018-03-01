package com.ziroom.bsrd;

public interface AsyncRPCCallback {

    void success(Object result);

    void fail(Exception e);

}
