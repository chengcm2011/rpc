package com.ziroom.bsrd.rpc.itf;

import com.ziroom.bsrd.rpc.decoder.Serializer;

public abstract class IServer {

    public abstract void start(final int port, final Serializer serializer) throws Exception;

    public abstract void destroy() throws Exception;
}
