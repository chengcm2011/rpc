package com.ziroom.bsrd.rpc.client;

import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.decoder.NettyDecoder;
import com.ziroom.bsrd.rpc.decoder.NettyEncoder;
import com.ziroom.bsrd.rpc.netty.RpcClientHandler;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import com.ziroom.bsrd.rpc.vo.RpcResponse;
import com.ziroom.bsrd.rpc.zk.ServiceNodeManange;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class NettyClient {

    private Channel channel;

    public void create(InetSocketAddress inetSocketAddress) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ServiceNodeManange.getInstance().getEventLoopGroup()).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new NettyEncoder(RpcRequest.class))
                                .addLast(new NettyDecoder(RpcResponse.class))
                                .addLast(new RpcClientHandler());
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        this.channel = bootstrap.connect(inetSocketAddress).sync().channel();
    }

    public Channel getChannel() {
        return this.channel;
    }

    public boolean isValidate() {
        if (this.channel != null) {
            return this.channel.isActive();
        }
        return false;
    }

    public void close() {
        if (this.channel != null) {
            if (this.channel.isOpen()) {
                this.channel.close();
            }
        }
        ApplicationLogger.info("netty channel close.");
    }

    public void send(RpcRequest request) throws Exception {
        this.channel.writeAndFlush(request).sync();
    }
}
