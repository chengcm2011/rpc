package app;

import com.ziroom.bsrd.client.IPersonService;
import com.ziroom.bsrd.client.Person;
import com.ziroom.bsrd.log.ApplicationLogger;
import com.ziroom.bsrd.rpc.IAsyncObjectProxy;
import com.ziroom.bsrd.rpc.RpcClient;
import com.ziroom.bsrd.rpc.itf.AsyncRPCCallback;
import com.ziroom.bsrd.rpc.netty.RPCFuture;
import com.ziroom.bsrd.rpc.zk.ServiceDiscovery;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 异步处理测试
 *
 * @author chengys4
 *         2018-03-02 15:40
 **/
public class ServiceCallbackTest {
    public static void main(String[] args) {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        serviceDiscovery.setRegistryAddress("10.16.37.112:3181");
        serviceDiscovery.setNamespace("rpc");
        RpcClient rpcClient = new RpcClient(serviceDiscovery);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            rpcClient.init();
            Thread.sleep(3000L);


            Thread.sleep(3000L);

            IAsyncObjectProxy client = rpcClient.createAsync(IPersonService.class);
            int num = 5;
            RPCFuture helloPersonFuture = client.call("GetTestPerson", "xiaoming", num);
            helloPersonFuture.addCallback(new AsyncRPCCallback() {
                @Override
                public void success(Object result) {
                    List<Person> persons = (List<Person>) result;
                    for (int i = 0; i < persons.size(); ++i) {
                        System.out.println(persons.get(i));
                    }
                    countDownLatch.countDown();
                }

                @Override
                public void fail(Exception e) {
                    System.out.println(e);
                    countDownLatch.countDown();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        rpcClient.stop();

        ApplicationLogger.info("End");
    }
}
