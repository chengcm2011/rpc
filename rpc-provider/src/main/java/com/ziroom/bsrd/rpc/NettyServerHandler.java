package com.ziroom.bsrd.rpc;

import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import com.ziroom.bsrd.rpc.vo.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * cheng
 * 2018-02-17
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, RpcRequest request) throws Exception {

        RpcServer.submit(new Runnable() {
            @Override
            public void run() {
                ApplicationLogger.info("receive RpcRequest:" + request.toString());
                // invoke
                RpcResponse response = RpcServer.invokeService(request, null);
                ApplicationLogger.info("return RpcRequest response:" + response.toString());
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        ApplicationLogger.info("Send response for request " + request.getRequestId());
                    }
                });
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ApplicationLogger.info("rpc provider netty server caught exception", cause);
        ctx.close();
    }
}
