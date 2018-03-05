package com.ziroom.bsrd.rpc.netty;

import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.RpcClient;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import com.ziroom.bsrd.rpc.vo.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;

/**
 * rpc 请求客户端
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private String node;
    private volatile Channel channel;
    private SocketAddress remoteAddress;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remoteAddress;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remoteAddress = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {

        ApplicationLogger.info("receive response:" + response.toString());

        String requestId = response.getRequestId();
        RPCFuture rpcFuture = RpcClient.pendingRPC.get(requestId);
        if (rpcFuture != null) {
            RpcClient.pendingRPC.remove(requestId);
            rpcFuture.done(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ApplicationLogger.error("client caught exception", cause);
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public RPCFuture sendRequest(RpcRequest request) {
//        final CountDownLatch latch = new CountDownLatch(1);
//        RPCFuture rpcFuture = new RPCFuture(request);
//        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) {
//                latch.countDown();
//            }
//        });
//        try {
//            latch.await();
//        } catch (InterruptedException e) {
//            ApplicationLogger.error(e.getMessage(), e);
//        }

        return null;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
