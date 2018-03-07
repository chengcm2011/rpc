package app;

import com.ziroom.bsrd.rpc.RpcClientProxy;
import com.ziroom.bsrd.rpc.test.IHelloService;
import com.ziroom.bsrd.rpc.test.Person;
import com.ziroom.bsrd.rpc.zk.ServiceDiscovery;

/**
 * @author chengys4
 *         2018-03-01 10:11
 **/
public class ServiceTest {
    public static void main(String[] args) throws Exception {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        serviceDiscovery.setRegistryAddress("10.16.37.112:3181");
        serviceDiscovery.setNamespace("rpc");
        RpcClientProxy rpcClient = new RpcClientProxy(IHelloService.class);
        IHelloService helloService = (IHelloService) rpcClient.getObject();
        Thread.sleep(1000);
        String result = helloService.hello("cheng");
        System.out.println(result);
        Thread.sleep(200);
        result = helloService.hello(new Person("cheng", "yingsheng"));
        System.out.println(result);
    }
}
