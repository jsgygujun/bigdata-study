# Flink

## 目录
* [Flink 基础知识](/flink/doc/Flink基础知识.md)
* [Flink 安装部署](/flink/doc/Flink安装部署.md)
* [Flink 流处理编程](/flink/doc/Flink流处理编程.md)

## Flink 集群架构

### 核心组件

Flink 核心架构的第二层是 Runtime 层，该层采用标准的 Master-Slave 结构，其中，Master部分又包含了三个核心组件：Dispatcher、ResourceManager 和 JobManager，而 Slave 则 主要是 TaskManager 进程。它们的功能分别如下：
* **JobManager** (也称为Master): 接收由 Dispatcher 传递过来的执行程序，该执行程序包含了作业图（JobGraph），逻辑数据流图（Logical Dataflow Graph）和所有的 class 文件以及第三方类库等。紧接着 JobManager 会将 JobGraph 转换为执行图 (ExecutionGraph)，然后向 ResourceManager 申请资源来执行该任务，一旦申请到资源，就将执行图分发给对应的 TaskManager。因此每个作业 (Job) 至少有一个 JobManager；高可用部署下可以有多个 JobManagers，其中一个作为 leader，其余的则处于 standby 状态。
* **TaskManager** (也称为Worker): TaskManager 负责实际的子任务 (subtasks) 的执行，每个 TaskManager 都拥有一定数量的 slots。Slot 是一组固定大小的资源的合集 (如计算能力，存储空间)。TaskManagers 启动后，会将其所拥有的 slots 注册到 ResourceManager 上，由 ResourceManager 进行统一管理。
* **Dispatcher**: 负责接收客户端提交的执行程序，并传递给 JobManager 。除此之外，它还提供了一个 WEB UI 界面，用于监控作业的执行情况。
* **ResourceManager**: 负责管理 slots 并协调集群资源。ResourceManager 接收来自 JobManager 的资源请求，并将存在空闲 slots 的 TaskManagers 分配给 JobManager 执行任务。Flink 基于不同的部署平台，如 YARN , Mesos，K8s 等提供了不同的资源管理器，当 TaskManager 没有足够的 slots 来执行任务时，它会向第三方平台发起会话来请求额外的资源。

