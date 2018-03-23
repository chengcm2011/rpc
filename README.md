# 部门分布式服务框架
## 简介
基于SpringBoot的分布式服务框架
### 特性说明
- 服务透明，封装了底层通信细节，调用远程服务就像调用本地服务；
- zookeeper作为服务注册中心 ,netty 作为通讯框架，hessian作为序列化
- 目前只支持同步调用，异步调用需要单独处理
### 项目结构说明
- rpc-admin 服务节点查看
- rpc-common 公共模块
- rpc-consumer 服务消费方
- rpc-provider 服务提供方
- rpc-registry 服务注册
- rpc-test 测试

### 如何接入
1. 添加maven依赖

目前版本是:1.0.0-SNAPSHOT

     服务提供方加入

     ```
     <dependency>
         <groupId>com.ziroom.bsrd</groupId>
         <artifactId>rpc-provider</artifactId>
         <version>1.0.0-SNAPSHOT</version>
     </dependency>
      ```
     服务消费方加入

     ```
     <dependency>
         <groupId>com.ziroom.bsrd</groupId>
         <artifactId>rpc-consumer</artifactId>
         <version>1.0.0-SNAPSHOT</version>
     </dependency>
     ```

2. SpringBoot配置文件中加入如下配置即可使用
 ```
rpc:
  server:
        port: 8087
  zookeeper:
        serverLists: XXX
        rpcnamespace: XXX

 ```
 或者
 ```
 rpc.server.port=8087
 rpc.zookeeper.serverLists=XXXXXX
 rpc.zookeeper.rpcnamespace=XXXXX
 ```

### 如何使用
1. 服务提供方
    ```
      //通过@RpcService的方式进行识别，value值为服务接口类
      @RpcService(IHelloService.class)
      @Service

    ```
2. 服务消费方
    - 初始化
    ```
        @Configuration
        public class RpcServiceConfig {

            @Bean
            public IHelloService helloService() throws Exception {
                RpcReferenceConfig rpcClientProxy = new RpcReferenceConfig(IHelloService.class);
                return (IHelloService) rpcClientProxy.getObject();
            }
        }
    ```
    - 使用

    ```
        @Resource
        IHelloService helloService;
    ```