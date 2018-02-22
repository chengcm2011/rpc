package com.ziroom.bsrd;

/**
 * cheng
 * 2018-02-16
 */
public abstract class IServer {

    public abstract void start(final int port, final Serializer serializer) throws Exception;

    public abstract void destroy() throws Exception;
}
