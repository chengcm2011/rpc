package com.ziroom.bsrd.rpc.transport;

import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.config.RpcReferenceConfig;
import com.ziroom.bsrd.rpc.vo.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * rpc 请求客户端
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {

        ApplicationLogger.info("receive response:" + response.toString());
        String requestId = response.getRequestId();
        RPCFuture rpcFuture = RpcReferenceConfig.pendingRPC.get(requestId);
        if (rpcFuture != null) {
            RpcReferenceConfig.pendingRPC.remove(requestId);
            rpcFuture.done(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ApplicationLogger.error("client caught exception", cause);
        ctx.close();
    }
}
