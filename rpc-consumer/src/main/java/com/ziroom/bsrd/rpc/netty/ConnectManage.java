package com.ziroom.bsrd.rpc.netty;

import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.itf.NodeType;
import com.ziroom.bsrd.rpc.vo.NodeVO;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RPC Connect Manage
 */
public class ConnectManage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectManage.class);
    private volatile static ConnectManage connectManage;

    EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
    private Map<String, List<RpcClientHandler>> connectedServerNodes = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    protected long connectTimeoutMillis = 6000;
    private AtomicInteger roundRobin = new AtomicInteger(0);
    private volatile boolean isRuning = true;

    private ConnectManage() {
    }

    public static ConnectManage getInstance() {
        if (connectManage == null) {
            synchronized (ConnectManage.class) {
                if (connectManage == null) {
                    connectManage = new ConnectManage();
                }
            }
        }
        return connectManage;
    }

    /**
     * 初始化服务器
     *
     * @param serviceNodes
     */
    public void initServices(Map<String, List<String>> serviceNodes) {

        for (Map.Entry<String, List<String>> serviceNode : serviceNodes.entrySet()) {
            String serviceName = serviceNode.getKey();
            List<String> nodeList = serviceNode.getValue();
//            connectServerNode(serviceName, nodeList);
        }

    }

    /**
     * 连接服务器
     *
     * @param serviceName
     * @param nodeList
     */
    private void connectServerNode(String serviceName, final List<String> nodeList) {

        for (String nodeinfo : nodeList) {
            InetSocketAddress remoteAddress = parseNode(nodeinfo);
            threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    Bootstrap b = new Bootstrap();
                    b.group(eventLoopGroup)
                            .channel(NioSocketChannel.class)
                            .handler(new RpcClientInitializer());

                    ChannelFuture channelFuture = b.connect(remoteAddress);
                    channelFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                LOGGER.info("service= {} successfully connect to remote - remote address ={} ", serviceName, remoteAddress);
                                RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
                                addHandler(serviceName, handler);
                            }
                        }
                    });
                }
            });
        }
    }


    /**
     * 解析服务器地址
     *
     * @param node
     * @return
     */
    private InetSocketAddress parseNode(String node) {
        String[] array = node.split(":");
        if (array.length == 2) {
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            final InetSocketAddress remotePeer = new InetSocketAddress(host, port);
            return remotePeer;
        }
        ApplicationLogger.info("node error ");
        return null;
    }

    /**
     * 添加
     *
     * @param serviceName
     * @param handler
     */
    private void addHandler(String serviceName, RpcClientHandler handler) {
        List<RpcClientHandler> rpcClientHandlerList = connectedServerNodes.get(serviceName);
        if (rpcClientHandlerList == null) {
            rpcClientHandlerList = new ArrayList<>();
        }
        rpcClientHandlerList.add(handler);
        connectedServerNodes.put(serviceName, rpcClientHandlerList);

    }

    private void removeHandler(String serviceName, String nodeStr) {
        List<RpcClientHandler> rpcClientHandlerList = connectedServerNodes.get(serviceName);
        if (rpcClientHandlerList == null) {
            rpcClientHandlerList = new ArrayList<>();
        }
        int removeIndex = 0;
        for (int i = 0; i < rpcClientHandlerList.size(); i++) {
            RpcClientHandler clientHandler = rpcClientHandlerList.get(i);
            if (clientHandler.getNode().equals(nodeStr)) {
                removeIndex = i;
            }
        }
        if (removeIndex != 0) {
            rpcClientHandlerList.remove(removeIndex);
        }
        connectedServerNodes.put(serviceName, rpcClientHandlerList);

    }

    private void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(this.connectTimeoutMillis, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 选择服务器
     *
     * @param serviceName
     * @return
     */
    public RpcClientHandler chooseHandler(String serviceName) {
        List<RpcClientHandler> handlers = connectedServerNodes.get(serviceName);
        int size = handlers.size();
        while (isRuning && size <= 0) {
            try {
                boolean available = waitingForHandler();
                if (available) {
                    size = handlers.size();
                }
            } catch (InterruptedException e) {
                LOGGER.error("Waiting for available node is interrupted! ", e);
                throw new RuntimeException("Can't connect any servers!", e);
            }
        }
        int index = (roundRobin.getAndAdd(1) + size) % size;
        RpcClientHandler selectRpcClientHandler = handlers.get(index);
        LOGGER.info("service={} select={}", serviceName, selectRpcClientHandler.getRemotePeer());
        return selectRpcClientHandler;
    }

    public void stop() {
        isRuning = false;
        for (Map.Entry<String, List<RpcClientHandler>> listEntry : connectedServerNodes.entrySet()) {
            List<RpcClientHandler> rpcClientHandlerList = listEntry.getValue();
            for (int i = 0; i < rpcClientHandlerList.size(); ++i) {
                RpcClientHandler connectedServerHandler = rpcClientHandlerList.get(i);
                connectedServerHandler.close();
            }
        }
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

    public void updateServicesNode(String data) {

    }

    public void removeServicesNode(String data) {
        NodeVO nodeVo = getNode(data);
        if (nodeVo.getType().equals(NodeType.NODE)) {
            removeServerNode(nodeVo);
        }
    }

    public void addServicesNode(String data) {
        NodeVO nodeVo = getNode(data);
        if (nodeVo.getType().equals(NodeType.NODE)) {
            addServerNode(nodeVo);
        }
    }


    private NodeVO getNode(String data) {
        String[] d = data.split(";");
        NodeVO nodeVo = new NodeVO(d[0]);
        if (NodeType.SERVICE.equals(nodeVo.getType())) {
            nodeVo.setServiceName(d[1]);
        }
        if (NodeType.NODE.equals(nodeVo.getType())) {
            nodeVo.setServiceName(d[1]);
            nodeVo.setNode(parseNode(d[2]));
            nodeVo.setNodeStr(d[2]);
        }
        return nodeVo;
    }


    private void addServerNode(NodeVO nodevo) {
        String serviceName = nodevo.getServiceName();
        InetSocketAddress remoteAddress = nodevo.getNode();
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new RpcClientInitializer());

                ChannelFuture channelFuture = b.connect(remoteAddress);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            LOGGER.info("service= {} successfully connect to remote - remote address ={} ", serviceName, remoteAddress);
                            RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
                            handler.setNode(nodevo.getNodeStr());
                            addHandler(serviceName, handler);
                        }
                    }
                });
            }
        });
    }

    private void removeServerNode(NodeVO nodeVo) {
        removeHandler(nodeVo.getServiceName(), nodeVo.getNodeStr());
    }
}
