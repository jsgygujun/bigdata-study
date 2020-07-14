# Hive

## 一、Hive 基本概念

### 1.1 什么是 Hive

Hive 是基于 Hadoop 的一个数据仓库工具，可以将结构化的数据文件映射为一张表，并提供类 SQL 查询功能。

本质： **将HQL转化为 MapReduce 程序**。

1. Hive 处理的数据存储在 HDFS上。
2. Hive 分析数据底层的实现是 MapReduce。
3. 执行程序运行在 Yarn 上。

### 1.2 Hive 的优缺点

#### 1.2.1 优点

1. 操作接口采用类 SQL 语法，提供快速开发的能力(简单、容易上手)
2. 避免了去写 MapReduce，减少开发人员的学习成本。
3. Hive 的执行延迟比较高，因此 Hive 常用于数据分析，对实时性要求不高的场合;
4. Hive 优势在于处理大数据，对于处理小数据没有优势，因为 Hive 的执行延迟比较高。
5. Hive 支持用户自定义函数，用户可以根据自己的需求来实现自己的函数。

#### 1.2.1 缺点

1. Hive 的 HQL 表达能力有限
   1. 迭代式算法无法表达
   2. 数据挖掘方面不擅长
2. Hive 的效率比较低
   1. Hive 自动生成的 MapReduce 作业，通常情况下不够智能化
   2. Hive 调优比较困难，粒度较粗

### 1.3 Hive 架构原理

Hive 通过给用户提供的一系列交互接口，接收到用户的指令(SQL)，使用自己的 Driver，结合元数据(MetaStore)，将这些指令翻译成 MapReduce，提交到 Hadoop 中执行，最后，将执行返回的结果输出到用户交互接口。

1. 用户接口：Client

   CLI(hive shell)、JDBC/ODBC(java 访问 hive)、WEBUI(浏览器访问 hive)

2. 元数据：Metastore

   元数据包括：表名、表所属的数据库(默认是 default)、表的拥有者、列/分区字段、表的类型(是否是外部表)、表的数据所在目录等。默认存储在自带的 derby 数据库中，**实际生产环境使用 MySQL 存储 Metastore**

3. Hadoop

   使用 HDFS 进行存储，使用 MapReduce 进行计算。

4. 驱动器：Driver

   1. 解析器(SQL Parser):将 SQL 字符串转换成抽象语法树 AST，这一步一般都用第三方工具库完成，比如 antlr；对 AST 进行语法分析，比如表是否存在、字段是否存在、SQL 语义是否有误。
   2. 编译器(Physical Plan):将 AST 编译生成逻辑执行计划。
   3. 优化器(Query Optimizer):对逻辑执行计划进行优化。
   4. 执行器(Execution):把逻辑执行计划转换成可以运行的物理计划。对于 Hive 来 说，就是 MR/Spark。

