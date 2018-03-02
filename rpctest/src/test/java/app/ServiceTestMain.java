package app;

import com.ziroom.bsrd.client.IHelloService;
import com.ziroom.bsrd.rpc.RpcClient;
import com.ziroom.bsrd.rpc.zk.ServiceDiscovery;

/**
 * @author chengys4
 *         2018-03-01 10:11
 **/
public class ServiceTestMain {
    public static void main(String[] args) throws Exception {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        serviceDiscovery.setRegistryAddress("10.16.37.112:3181");
        serviceDiscovery.setNamespace("rpc");
        RpcClient rpcClient = new RpcClient(serviceDiscovery);
        rpcClient.init();
        Thread.sleep(6000L);
        IHelloService helloService = rpcClient.create(IHelloService.class);
        String result = helloService.hello("World");
        System.out.println(result);

        Thread.sleep(1000L);

        rpcClient.stop();
    }
}
