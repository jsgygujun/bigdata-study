# Flink 流处理编程

## 一、 Flink 流处理 API

* 编程模型

  Environment -> Source -> Transformation -> Sink

* Environment

  ```scala
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  ```

  * 独立执行
  * 提交到集群执行

* Source

  * 从集合读取数据
  * 从文件读取数据
  * 从 Kafka 读取数据
  * 自定义 Source

* Transformation 转换算子

  * 基本转换算子
  * 分区转换算子
  * 多流转换算子

* Sink

  * Kafka
  * Redis
  * Elasticsearch
  * JDBC

* 支持的数据类型

  * 基础数据类型
  * Java 和 Scala 元组
  * Scala 样例类
  * Java 简单对象 POJO
  * 其他（Arrays, List, Maps, Enums 等）

* 实现 UDF 

  * 函数类
  * 匿名函数
  * 富函数
    * 函数类的增强版本
    * 有生命周期方法，如`open()`, `close()`
    * 可获取运行时上下文，在运行时可以对状态进行操作
    * 状态编程依靠富函数实现

## 二、 Flink 中的 Window

## 三、 Flink 中的时间语义

* EventTime
  * 事件创建的时间
  * 由事件中的时间戳描述，如采集的日志数据，每一条日志都会记录自己生成的时间戳
  * Flink 通过时间戳分配器访问事件时间戳
  * 在 Flink 的流式处理中，绝大部分的业务都会使用 eventTime，只有在 eventTime无法使用，才被迫使用 ProcessingTime 或者     IngestionTime
* ProcessingTime
  * 数据进入 Flink 的时间
* IngestionTime
  * 每一个执行基于时间操作的算子的本地系统时间
  * 与机器相关，也是默认的时间属性

## 四、 Flink 中的水位线

## 五、 Flink 状态编程和容错机制

### 5.1 有状态的算子和应用程序

map/fliter/flatMap 算子本来是没有状态的，但是可以通过实现 RichFunction，在其中自定义状态进行操作。

Reduce/aggregate/window 算子本来就是有状态的，是 Flink 底层执行管理的

* 算子状态（Operator State）
* 键控状态 （Keyed State）
  * ValueState
    * ValueState.value()
    * ValueState.update(value)
  * ListState
    * ListState.add(value)
    * ListState.addAll(values)
    * ListState.get()
    * ListState.update(values)
  * MapState
    * MapState.get(key)
    * MapState.put(key, value)
    * MapState.contains(key)
    * MapState.remove(key)
  * ReductingState
  * AggregatingState

### 5.2 状态后端

状态后端负责存储每个状态实例的本地状态，并在生成 checkpoint 时将其写入远程持久化存储中。

* MemoryStateBackend

  * 状态存在本地内存中（存储状态量小，存在GC停顿的问题）
  * 执行 checkpoint 时将状态存到 JobManager 的内存中（一旦 JobManager 出现故障，状态丢失）

* FsStateBackend

  * 状态存在本地内存中（存储状态量小，存在GC停顿的问题）
  * 执行 checkpoint 时将状态保存到远程可靠存储中

* RocksDBStateBackend

  * 状态存储在嵌入式数据库中（速度慢但是存储状态量大）
  * 执行 checkpoint 时将状态保存到远程可靠存储（支持增量checkpoint）

  ```scala
  val checkpointPath = ""
  val backend = new RocksDBStateBackend(checkpointPath)
  env.setStateBackend(backend) // 设置状态后端
  ```

### 5.3 状态一致性

* 一致性级别

  * At-most-once 没有正确性保障，如 UDP 协议
  * At-least-once
  * Exact-once

* 端到端（end-to-end）状态一致性

  在流处理应用中，处理流处理器需要保证一致性外还需要数据源和输出端的一致性。端到端的一致性保证意味着结果的正确性贯穿了整个流处理应用的始终，每个组件都保证了它自己的一致性，整个端到端的一致性级别取决于所有组件中一致性最弱的组件，具体划分如下：

  * 内部保证 —— 依赖 checkpoint 机制
  * Source 端 —— 具有重置数据的读取位置
  * Sink 端 —— 当发生故障恢复时，数据不会重复写入功能。两种实现方式
    * 幂等写入： 一个操作执行一次和执行多次的结果是一样的，如 Map.put(key, value)
    * 事务写入： 通过构建事务来写入外部系统，构建的事务对应着 checkpoint，等到 checkpoint 真正完成的时候，才把对应的结果写入 sink 系统中。两种实现方式
      * 预写日志（WAL）
      * 两阶段提交（2PC）

### 5.4 检查点（checkpoint）







