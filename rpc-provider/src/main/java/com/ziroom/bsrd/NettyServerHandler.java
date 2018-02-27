package com.ziroom.bsrd;

import com.ziroom.bsrd.server.RpcServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * cheng
 * 2018-02-17
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);


    @Override
    public void channelRead0(final ChannelHandlerContext ctx, RpcRequest request) throws Exception {

        // invoke
        RpcResponse response = RpcServer.invokeService(request, null);

        ctx.writeAndFlush(response);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(">>>>>>>>>>> xxl-rpc provider netty com.ziroom.bsrd.server caught exception", cause);
        ctx.close();
    }
}
