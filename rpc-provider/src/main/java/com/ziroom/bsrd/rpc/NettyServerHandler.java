package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import com.ziroom.bsrd.rpc.vo.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * cheng
 * 2018-02-17
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, RpcRequest request) throws Exception {

        // invoke
        RpcResponse response = RpcServer.invokeService(request, null);

        ctx.writeAndFlush(response);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ApplicationLogger.info("rpc provider netty server caught exception", cause);
        ctx.close();
    }
}
