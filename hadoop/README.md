# Hadoop

## 一、概念

### 1.1 Hadoop是什么？

1. Hadoop是一个由Apache基金会所开发的**分布式系统基础架构**。
2. 主要解决**海量**数据的**存储**和**分析计算**问题。
3. 广义上来讲，Hadoop指的是一个更广泛的概念： **Hadoop生态圈**。

## 1.2 Hadoop组成

1. Hadoop HDFS: 高可靠、高吞吐量的分布式文件系统。
2. Hadoop MapReduce： 一个分布式的离线并行计算框架。
3. Hadoop YARN： 作业调度和集群资源管理的框架。
4. Hadoop Common： 支持其他模块的工具模块（Configuration、RPC、序列化机制、日志操作等）

## 1.3 Hadoop的优势

1. 高可靠性： Hadoop维护多个工作数据副本，当出现故障时可以对失败的节点重新分配处理。
2. 高扩展性： 在集群之间分配任务数据，可方便的扩展以千计的节点。
3. 高效性： 在Mapreduce的思想下，Hadoop是并行工作的，以加快任务处理速度。
4. 高容错性： 自动保存多份副本数据，并且能够自动讲失败的任务重新分配。

## 二、目录

* [HDFS](HDFS.md)
* [YARN](YARN.md)
* [MapReduce](MapReduce.md)
* [Hadoop集群搭建](DEVOPS.md)