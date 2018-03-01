package app;

import com.ziroom.bsrd.RpcClient;
import com.ziroom.bsrd.ServiceDiscovery;
import com.ziroom.bsrd.client.IHelloService;
import org.junit.Assert;

/**
 * @author chengys4
 *         2018-03-01 10:11
 **/
public class St {
    public static void main(String[] args) throws Exception {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        RpcClient rpcClient = new RpcClient(serviceDiscovery);
        rpcClient.setServicesAddress("10.16.37.112:3181");
        rpcClient.init();
        Thread.sleep(10000L);
        IHelloService helloService = rpcClient.create(IHelloService.class);
        String result = helloService.hello("World");
        System.out.println(result);
        Assert.assertEquals("Hello! World", result);

        Thread.sleep(10000L);

        rpcClient.stop();
    }
}
