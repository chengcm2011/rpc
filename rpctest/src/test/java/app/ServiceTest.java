package app;

import com.ziroom.bsrd.client.IHelloService;
import com.ziroom.bsrd.client.IPersonService;
import com.ziroom.bsrd.client.Person;
import com.ziroom.bsrd.rpc.RpcClient;
import com.ziroom.bsrd.rpc.zk.ServiceDiscovery;

import java.util.List;

/**
 * @author chengys4
 *         2018-03-01 10:11
 **/
public class ServiceTest {
    public static void main(String[] args) throws Exception {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        serviceDiscovery.setRegistryAddress("10.16.37.112:3181");
        serviceDiscovery.setNamespace("rpc");
        RpcClient rpcClient = new RpcClient(serviceDiscovery);
        rpcClient.init();
        Thread.sleep(1000);
        IHelloService helloService = rpcClient.create(IHelloService.class);
        String result = helloService.hello("cheng");
        System.out.println(result);
        Thread.sleep(200);
        result = helloService.hello(new Person("cheng", "yingsheng"));
        System.out.println(result);
        IPersonService personService = rpcClient.create(IPersonService.class);
        List<Person> d = personService.GetTestPerson("cheng", 1);
        System.out.println(d.toString());
        rpcClient.stop();
    }
}
