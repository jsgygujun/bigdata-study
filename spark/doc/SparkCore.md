# Spark Core

## 一、 RDD 概念

RDD（Resilient Distributed Dataset）弹性分布式数据集，是 Spark 最基本、最重要的数据抽象。

RDD 特点

* 弹性

  * 存储的弹性： 内存与磁盘的自动切换
  * 容错的弹性： 数据丢失可以自动恢复
  * 计算的弹性： 计算出错可以自动重试
  * 分片的弹性： 可以根据需要重新分片

* 分区

* 只读

* 依赖（血缘关系）

  RDDs 通过操作算子进行转换，转换得到新的 RDD 包含了从其它 RDDs 衍生所必须的信息，RDDs 之间维护着这种血缘关系，也称之为依赖。RDD 有两种依赖：

  * 窄依赖： RDDs 之间分区是一一对应的。
  * 宽依赖： 下游 RDD 的每个分区与上游 RDD 的每个分区都有关系，是多对多的关系。

* 缓存

* Checkpoint 机制

  血缘关系太长会导致计算失败时重建 RDD 代价高，因此可将 RDD 持久化到存储中，切断之前的血缘关系

## 二、 RDD 编程

### 2.1 编程模型

创建 RDD -> 进行一系列 RDD 转换 -> 调用action 触发 RDD 计算并向应用程序返回计算结果。

编写一个 Driver 程序，提交到集群调度运行 Workers。

### 2.2 RDD 创建

* 从外部存储创建

  * 本地文件

    ```scala
    val rdd = sc.textFile(path/to/file)
    ```

  * Hadoop 支持的存储数据源

  >* url 可以是本地文件系统，hdfs://...，s3n://...等等
  >* 如果是使用的本地文件系统的路径, 则必须每个 Worker 节点都要存在这个路径
  >* 所有基于文件的方法, 都支持目录、压缩文件和通配符(*)
  >* textFile 还可以有第二个参数，表示分区数。默认情况下，每个块对应一个分区(对 HDFS 来说， 块大小默认是 128M)。可以传递一个大于块数的分区数，但是不能传递一个比块数小的分区数。

* 从其他 RDD / DataFrame / DataSet 创建

* 从 Scala 集合创建

  * sc.parallelize

    ```scala
    val rdd = sc.parallelize(List(1,2,3,4,5,6,7,8,9))
    ```

  * sc.makeRDD

    ```scala
    val rdd = sc.makeRDD(Array(9,8,7,6,5,4,3,2,1))
    ```

> [参考代码](src/main/scala/com/jsgygujun/code/spark_core/create/CreateRDDExp.scala)

### 2.3 RDD 转换算子

### 2.4 RDD 行动算子

### 2.5 RDD 传递函数和变量

### 2.6 RDD 依赖关系

### 2.7 Spark Job 划分

### 2.8 RDD 持久化

### 2.9 RDD checkpoint

### 2.10 变量共享

### 2.11 累加器

### 2.12 广播变量

## 三、 Key-Value 类型 RDD 的数据分区

## 四、 文件中数据的存储和保存







