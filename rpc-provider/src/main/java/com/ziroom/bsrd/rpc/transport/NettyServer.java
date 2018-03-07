package com.ziroom.bsrd.rpc.transport;


import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.coder.NettyDecoder;
import com.ziroom.bsrd.rpc.coder.NettyEncoder;
import com.ziroom.bsrd.rpc.serializer.Serializer;
import com.ziroom.bsrd.rpc.vo.RpcRequest;
import com.ziroom.bsrd.rpc.vo.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * cheng
 * 2018-02-16
 */
public class NettyServer {

    private Thread thread;

    public void start(final int port, final Serializer serializer) throws Exception {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel channel) throws Exception {
                                    /**
                                     * 需要按顺序来 服务器先解码 在编码
                                     * @param socketChannel
                                     * @throws Exception
                                     */
                                    channel.pipeline()
                                            .addLast(new NettyDecoder(RpcRequest.class, serializer))
                                            .addLast(new NettyEncoder(RpcResponse.class, serializer))
                                            .addLast(new NettyServerHandler());
                                }
                            })
                            .option(ChannelOption.SO_TIMEOUT, 100)
                            .option(ChannelOption.SO_BACKLOG, 128)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .option(ChannelOption.SO_REUSEADDR, true)
                            .childOption(ChannelOption.SO_KEEPALIVE, true);
                    ChannelFuture future = bootstrap.bind(port).sync();
                    ApplicationLogger.info("rpc server start success, server={}, port={}", NettyServer.class.getName(), port);
                    future.channel().closeFuture().sync().channel();
                } catch (InterruptedException e) {
                    ApplicationLogger.error("", e);
                } finally {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

    }
}
