package com.ziroom.bsrd.rpc.netty;


import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.NettyServerHandler;
import com.ziroom.bsrd.rpc.decoder.NettyDecoder;
import com.ziroom.bsrd.rpc.decoder.NettyEncoder;
import com.ziroom.bsrd.rpc.decoder.Serializer;
import com.ziroom.bsrd.rpc.itf.IServer;
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

    @Override
    public void destroy() throws Exception {
        thread.interrupt();
        ApplicationLogger.info("rpc server destroy success, server={}", NettyServer.class.getName());
    }

}
