# Flink 基础知识

## 一、 Flink 运行时组件

* JobManager （作业管理器）
  * JobManager 会先接收到要执行的应用程序，包括作业图(JobGraph)、逻辑数据流图(LOgical Dataflow Graph)、JAR包
  * JobManager 将JobGraph 转换成执行图(ExecutionGraph)，包含所有可以并发执行的任务
  * JobManager 将执行图发到真正运行它们的 TaskManager 上
  * 运行过程中，JobManager 负责所有需要中央协调的操作，比如检查点(checkpoint)的协调
* ResourceManager （资源管理器）
  * 管理 TaskManager 的Slot
  * JobManager 申请 Slot 时，ResourceManager 将由空闲 Slot 的TaskManager 分配给 JobManager
  * 负责终止空闲的  TaskManager，释放计算资源
* TaskManager （任务管理器）
  * 每一个 TaskManager 包含一定数量的 Slot
  * Slot 的数量限制了 TaskManager 能够执行的任务数量
  * TaskManager 向 ResourceManager 注册它的 Slot
  * 收到 ResourceManager 命令后 TaskManager 会将一个或者多个 Slot 提供给 JobManager 调用，JobManager 就可以向插槽分配任务（tasks）来执行
  * 在执行过程中，一个 TaskManager 可以跟其它运行同一应用程序的 TaskManager 交换数据
* Dispatcher （分发器）
  * 当一个应用被提交执行时，分发器就会启动并将应用移交给一个JobManager
  * 提供 REST 接口作为集群的一个 HTTP 接入点
  * Dispatcher 也会启动一个Web UI，用来方便展示和监控作业执行的信息
  * Dispatcher 在架构中并不是必需的，取决于应用提交运行的方式