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

## 四、 Flink 中的水位线

## 五、 Flink 状态编程和容错机制





