

# Flink 流处理编程

## 一、 Flink 流处理 API

* 编程模型

  Environment -> Source -> Transformation -> Sink

* Environment

  根据上下文自动创建环境（本地环境、提交到远程集群的环境）

  ```scala
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  ```

  * 独立执行

    ```scala
    val env = StreamExecutionEnvironment.createLocalEnvironment()
    ```

  * 提交到集群执行

    ```scala
    val env = StreamExecutionEnvironment.createRemoteEnvironment(host: String, port: Int, jarFiles: String*)
    ```

* Source

  使用 `StreamExecutionEnvironment.addSource(sourceFunction)` 来为应用程序添加输入源。

  * 从集合读取数据
    * fromCollection(Seq)
    * fromCollection(Iterator)
    * fromElement(element: _*)
    * fromParallelCollections(SplittableIterator)
    * generateSequence(from, to)
  * 从文件读取数据
    * readTextFile(path)
    * readFile(fileInputFormat, path)
    * readFile(fileInputFormat, path, watchType, interval, pathFilter)
  * 自定义 Source，如从 Kafka 读取 
    * addSource
      * addSource(new FlinkKafkaConsumer010<>(...))

* Transformation 转换算子

  * 基本转换算子

    * map

      * 一进一出

        ```scala
        dataStream.map(x => x * 2)
        ```

    * flatMap

      * 一进N出，N=0,1,2...

        ```scala
        dataStream.flatMap(_.split("\t"))
        ```

    * filter

      * 过滤

        ```scala
        dataStream.filter(_ != 0)
        ```

  * 分区转换算子

    * keyBy

      * 在逻辑上将一个流分成不相交的分区，每个分区包含相同键的元素。在内部，这是通过哈希分区实现的。

        ```scala
        dataStream.keyBy("someKey")
        dataStream.keyBy(0)
        ```

    * reduce

      * 合并当前的元素和上次聚合的结果，产生一个新的值，返回的流中包含每一次聚合的结果，而不是只返回最后一次聚合的最终结果

        ```scala
        keyedStream.reduce { _ + _ }
        ```

    * fold

      * 具有初始值的键控数据流上的“滚动”折叠。将当前元素与最后折叠的值合并，并发出新值。

        ```scala
        // 一种折叠函数，当应用于序列（1,2,3,4,5）时，发出序列“start-1”，“start-1-2”，“start-1-2-3”。。
        val result: DataStream[String] = keyedStream.fold("start")((str, i) => str + "-" + i)
        ```

    * aggregations

      * 在键控数据流上滚动聚合。min和minBy之间的区别在于min返回最小值，而minBy返回该字段中具有最小值的元素（max和maxBy也是一样）。

        ```scala
        keyedStream.sum(0)
        keyedStream.sum("key")
        keyedStream.min(0)
        keyedStream.min("key")
        keyedStream.max(0)
        keyedStream.max("key")
        keyedStream.minBy(0)
        keyedStream.minBy("key")
        keyedStream.maxBy(0)
        keyedStream.maxBy("key")
        ```

  * 多流转换算子

* Sink

  * writeAsText() / TextOutputFormat
  * writeAsCsv(...) / CsvOutputFormat
  * print() / printToErr()
  * writeUsingOutputFormat()
  * writeToSocket
  * addSink
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







