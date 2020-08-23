# Spark 基础知识

## 一、 Spark 角色
* Master
* Worker
* Driver Program
* Executor

## 二、 Spark 运行模式
* 本地模式
* 集群模式

  * Standalone 模式
  
    * 一个 Master 节点（ClusterManager）+ 多个 Slave 节点（WorkerNode）构成 Spark 集群
    * Spark 应用程序由驱动器提交到集群中运行
  * Hadoop YARN 模式
  
     Spark 直接连接 YARN，无需额外构建 Spark 集群，根据 Driver 程序运行节点不同有YARN- Client 和 YARN-Cluster 两种模式
     * YARN-Client 运行流程
     * YARN-Cluster 运行流程
     * 两种模式对比
  * Apache Mesos 模式
  * Kubernets 模式