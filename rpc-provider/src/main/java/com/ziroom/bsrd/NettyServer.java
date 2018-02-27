package com.ziroom.bsrd;


import com.ziroom.bsrd.log.ApplicationLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * cheng
 * 2018-02-16
 */
public class NettyServer extends IServer {

    private Thread thread;

    @Override
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
                    ApplicationLogger.info("rpc com.ziroom.bsrd.server start success, com.ziroom.bsrd.server={}, port={}", NettyServer.class.getName(), port);
                    Channel serviceChannel = future.channel().closeFuture().sync().channel();
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

    @Override
    public void destroy() throws Exception {
        thread.interrupt();
        ApplicationLogger.info("rpc com.ziroom.bsrd.server destroy success, com.ziroom.bsrd.server={}", NettyServer.class.getName());
    }

}
