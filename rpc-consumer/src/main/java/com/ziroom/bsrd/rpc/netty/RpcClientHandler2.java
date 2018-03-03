package com.ziroom.bsrd.rpc.netty;

import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.CountDownLatch;

/**
 * cheng
 * 2018-03-03
 */
public class RpcClientHandler2 {

    private Channel channel;

    private String node;

    public RPCFuture sendRequest(RpcRequest request) {

        final CountDownLatch latch = new CountDownLatch(1);
        RPCFuture rpcFuture = new RPCFuture(request);
//        pendingRPC.put(request.getRequestId(), rpcFuture);
//        channel.writeAndFlush(request);

        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            ApplicationLogger.error(e.getMessage(), e);
        }

        return rpcFuture;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public void close() {
        channel.close();

    }
}
