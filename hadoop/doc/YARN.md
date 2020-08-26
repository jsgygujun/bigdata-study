# YARN

YARN 是一个资源调度平台，负责为运算程序提供服务器运算资源，相当于一个分布式的**操作系统平台**，而 MapReduce 等运算程序则相当于运行于操作系统之上的应用程序。

## 一、YARN 架构概述

YARN 主要由 ResourceManager、NodeManager、ApplicationMaster 和 Container 等组件构成。

1. ResourceManager(rm)： 处理客户端请求、启动并监控ApplicationMaster、监控NodeManager、资源分配与调度。
2. NodeManager(nm)： 单个节点上的资源管理、处理来自ResourceManager的命令、处理来自ApplicationMaster的命令。
3. ApplicationMaster： 数据切分、为应用程序申请资源并分配给内部任务、任务监控与容错。
4. Container： 对任务运行环境的抽象，封装了CPU、内存等多维资源以及环境变量、启动命令等任务运行相关的信息。

## 二、YARN工作机制

1. MR 程序提交到客户端所在的节点。
2. YarnRunner 向 ResourceManager 申请一个 Application。
3. RM 将该应用程序的资源路径（hdfs://.../.staging/application_id）返回给 YarnRunner。
4. 该程序将运行所需资源提交到 HDFS 上。
5. 程序资源提交完毕后，申请运行 mrAppMaster。
6. RM 将用户的请求初始化成一个 Task。
7. 其中一个 NodeManager 领取到 Task 任务。
8. 该 NodeManager 创建容器 Container，并产生 MRAppmaster。
9. Container 从 HDFS 上拷贝资源到本地。
10. MRAppmaster 向 RM 申请运行 MapTask 资源。
11. RM 将运行 MapTask 任务分配给另外两个 NodeManager，另两个 NodeManager 分别领取任务并创建容器。
12. MR 向两个接收到任务的 NodeManager 发送程序启动脚本，这两个 NodeManager 分别启动 MapTask，MapTask 对数据分区排序。
13. MrAppMaster 等待所有 MapTask 运行完毕后，向 RM 申请容器，运行 ReduceTask。
14. ReduceTask 向 MapTask 获取相应分区的数据。
15. 程序运行完毕后，MR 会向 RM 申请注销自己。