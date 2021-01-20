# ZooKeeper

## 一、ZooKeeper 简介

### 1.1 概述

ZooKeeper 是一个开源的分布式的，为分布式应用提供协调服务的 Apache 项目。

> ZooKeeper 的出现让分布式应用开发人员可以更多关注应用本身的逻辑，而不是协同工作上。

从设计模式角度来理解，ZooKeeper 是一个基于观察者模式设计的分布式服务管理框架，它负责存储和管理分布式应用各个节点都关心的数据，然后接受观察者的注册，一旦这些数据的状态发生变化，它就将负责通知这些观察者们作出响应的反应。

>ZooKeeper = 文件系统 + 通知机制

### 1.2 应用场景

ZooKeeper 能够提供的服务包括： 统一命名服务、统一配置管理、统一集群管理、服务器节点动态上下线、软负载均衡等。

* Apache HBase

  ZooKeeper 用于选举一个集群内的主节点，以便跟踪可用的服务器，并保存集群的元数据。

* Apache Kafka

  ZooKeeper 用于检测崩溃，实现主题（Topic）的发现，并保持主题的生产和消费状态。

* Apache Solor

  ZooKeeper 用于存储并协作更新集群元数据。

### 1.3 不适用场景

ZooKeeper 不适合用作海量数据存储，对于海量应用数据的存储应该选择分布式数据库或者分布式文件系统。

## 二、 ZooKeeper 架构 & 原理

### 2.1 节点类型

* 持久节点（Persistent）

  客户端和服务器断开连接后，创建的节点不删除。

* 临时节点（Ephemeral）

  客户端和服务器断开连接后，创建的节点删除。

* 顺序编号节点

  ZooKeeper 给该节点名称进行顺序编号。

  >创建 zone 时设置顺序标识，znode 名称会附加一个值，顺序号是一个单调递增的计数器，由父节点维护。
  >
  >注意： 分布式系统中，顺序号可以被用于为所有的事件进行全局排序，这样客户端可以通过顺序号推断事件的顺序。

### 2.2 监听器原理

1. 首先要有一个 main 线程
2. 在 main 线程中创建 ZooKeeper 客户端，这时就会创建两个线程，一个负责网络连接通信（connect），一个负责监听（listener）
3. 通过 connect 线程将注册的监听事件发送给 ZooKeeper
4. 在 ZooKeeper 的注册监听器列表中将注册的监听事件添加到列表中
5. ZooKeeper 监听到有数据或路径变化，就会将这个消息发送给 listener 线程
6. Listener 线程内部调用 `process()` 方法

### 2.3 Paxos 算法

### 2.4 选举机制

### 2.5 写数据流程

## 三、 ZooKeeper 常用命令

### 3.1 集群命令

* 启动集群

  ```shell
  zkServer.sh start
  ```

* 查看状态

  ```shell
  zkServer.sh status
  ```

* 停止集群

  ```shell
  zkServer.sh stop
  ```

### 3.2 客户端命令

* 启动客户端

  ```shell
  zkCli.sh
  ```

* 推出客户端

  ```shell
  quit
  ```

  

* 显示所有操作命令

  ```shell
  help
  ```

* 查看当前 zone 中所包含的内容

  ```shell
  ls /
  ```

* 查看当前 zone 详细数据

  ```shell
  ls2 /
  ```

* 创建普通节点

  ```shell
  create /path/to/znode "data"
  ```

* 获得节点的数据

  ```shell
  get /path/to/znode
  ```

* 创建临时节点

  ```shell
  create -e /path/to/znode "data"
  ```

* 创建带序号的节点

  ```shell
  create -s /path/to/znode/node1 "node1"
  create -s /path/to/znode/node2 "node2"
  ```

* 修改节点数据

  ```shell
  set /path/to/znode "new-data"
  ```

* 监听节点数据

  ```shell
  get /path/to/znode watch
  ```

* 监听节点的子节点

  ```shell
  ls /path/to/parent/znode watch
  ```

* 删除节点

  ```shell
  delete /path/to/znode
  ```

* 递归删除节点

  ```shell
  rmr /path/to/parent/znode
  ```

* 查看节点状态

  ```shell
  stat /path/to/znode
  ```

## 四、ZooKeeper 常用 Java API

### 4.1 POM 依赖

```xml
<dependency>
  <groupId>org.apache.zookeeper</groupId>
  <artifactId>zookeeper</artifactId>
  <version>3.4.14</version>
</dependency>
```

### 4.2 Java API

* 创建 ZK 客户端

  ```java
  private static final String CONNECT_STRING = "hadoop-11:2181,hadoop-12:2181,hadoop-13:2181";
  private static final int SESSION_TIMEOUT = 2000;
  private ZooKeeper zkClient;
  @Before
  public void setUp() throws Exception {
    zkClient = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, new Watcher() {
      @Override
      public void process(WatchedEvent watchedEvent) {
        // 收到事件通知后的回调函数（用户的业务逻辑）
        System.out.println(watchedEvent.getType() + "--" + watchedEvent.getPath());
        // 再次启动监听
        try {
          zkClient.getChildren("/", true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  ```

  

* 创建节点

  ```java
  @Test
  public void create() throws Exception {
    String znode = zkClient.create("/root", "rootValue".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    System.out.println(znode);
  }
  ```

* 获取子节点数据并监听节点变化

  ```java
  @Test
  public void getChildrenAndWatch() throws Exception {
    List<String> children = zkClient.getChildren("/", true);
    for (String child : children) {
      System.out.println(child);
    }
    // 延迟阻塞
    Thread.sleep(1000*15);
  }
  ```

* 判断 znode 是否存在

  ```java
  @Test
  public void exist() throws Exception {
    Stat stat = zkClient.exists("/root", false);
    System.out.println(stat == null ? "not exist" : "exist");
  }
  ```

  ### 4.3 使用 Curator 

  Curator 是 Netflix 公司开源的一套 Zookeeper 客户端框架，解决了很多 Zookeeper 客户端非常底层的细节开发工作，包括连接重连、反复注册Watcher和NodeExistsException异常等等。

## 五、 ZooKeeper 集群搭建



## 六、 ZooKeeper Q & A



 