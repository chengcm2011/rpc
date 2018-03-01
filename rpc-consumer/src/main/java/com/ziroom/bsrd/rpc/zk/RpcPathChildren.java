package com.ziroom.bsrd.rpc.zk;

import com.ziroom.bsrd.rpc.netty.ConnectManage;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc子节点(服务节点变动)
 *
 * @author chengys4
 *         2018-03-01 16:00
 **/
public class RpcPathChildren implements PathChildrenCacheListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcPathChildren.class);
    private String rpcPath;
    private CuratorFramework curatorFramework;

    public RpcPathChildren(final CuratorFramework curatorFramework, String rpcPath) {
        this.rpcPath = rpcPath;
        this.curatorFramework = curatorFramework;
    }

    public RpcPathChildren(final CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
        this.rpcPath = "/";
    }


    public void start() throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, rpcPath, true);
        pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
        pathChildrenCache.getListenable().addListener(this);
    }


    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
        switch (event.getType()) {
            case CHILD_ADDED:
                LOGGER.info(event.getType() + "--" + event.getData().getPath() + "--" + new String(event.getData().getData()));
                ChildData childData = event.getData();
                ConnectManage.getInstance().addServicesNode(new String(childData.getData()));
                break;
            case CHILD_REMOVED:
                LOGGER.info(event.getType() + "--" + event.getData().getPath() + "--" + new String(event.getData().getData()));
                ConnectManage.getInstance().removeServicesNode(new String(event.getData().getData()));
                break;
            case CHILD_UPDATED:
                LOGGER.info(event.getType() + "--" + event.getData().getPath() + "--" + new String(event.getData().getData()));
                ConnectManage.getInstance().updateServicesNode(new String(event.getData().getData()));
                break;
            default:
                break;
        }
    }
}
