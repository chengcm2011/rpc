package com.ziroom.bsrd.rpc.netty;

import com.ziroom.bsrd.rpc.decoder.HessianSerializer;
import com.ziroom.bsrd.rpc.decoder.NettyDecoder;
import com.ziroom.bsrd.rpc.decoder.NettyEncoder;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import com.ziroom.bsrd.rpc.vo.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 需要按顺序来 客户端 先编码 在解码
     *
     * @param socketChannel
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new NettyEncoder(RpcRequest.class, new HessianSerializer()));
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new NettyDecoder(RpcResponse.class, new HessianSerializer()));
        cp.addLast(new RpcClientHandler());
    }
}
