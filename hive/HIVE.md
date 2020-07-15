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





## 二、Hive 客户端安装

## 三、Hive 数据类型

## 四、DDL 数据定义

## 五、DML 数据操作

## 六、查询

## 七、函数

## 八、压缩和存储

## 九、调优

### 9.1 Fetch 抓取

Fetch 抓取是指，Hive 中对与某些情况的查询可以不必使用 MapReduce 计算。例如: `SELECT * FROM employees;`,在这种情况下，Hive 可以简单地读取 employee 对应的存储目录下的文件，然后输出查询结果到控制台。

在 `hive-default.xml.template` 文件中 `hive.fetch.task.conversion` 默认是 more，表示在全局查找、字段查找、limit 查找等都不走 MapReduce。如果把 `hive.fetch.task.conversion` 设置成 none，然后执行查询语句，都会执行 mapreduce 程序。

### 9.2 本地模式

大多数的 Hadoop Job 是需要 Hadoop 提供的完整的可扩展性来处理大数据集的。不过，有时 Hive 的输入数据量是非常小的。在这种情况下，为查询触发执行任务时消耗可能会比 实际 job 的执行时间要多的多。对于大多数这种情况，Hive 可以通过本地模式在单台机器上 处理所有的任务。对于小数据集，执行时间可以明显被缩短。

用户可以通过设置 `hive.exec.mode.local.auto` 的值为 true，来让 Hive 在适当的时候自动 启动这个优化。

`set hive.exec.mode.local.auto.inputbytes.max=50000000;//设置 local mr 的最大输入数据量，当输入数据量小于这个值时采用 local mr 的方式， 默认为 134217728，即 128M。

`set hive.exec.mode.local.auto.input.files.max=10;`//设置 local mr 的最大输入文件个数，当输入文件个数小于这个值时采用 local mr 的方式， 默认为 4

### 9.3 表的优化

#### 9.3.1 小表、大表 JOIN

将 key 相对分散，并且数据量小的表放在 join 的左边，这样可以有效减少内存溢出错误 发生的几率；再进一步，可以使用 Group 让小的维度表(1000 条以下的记录条数)先进内存。在 map 端完成 reduce。

实际测试发现：新版的 Hive 已经对小表 JOIN 大表和大表 JOIN 小表进行了优化。小表放在左边和右边已经没有明显区别。

#### 9.3.2 大表 JOIN 小表

1. 空 KEY 过滤

   有时 join 超时是因为某些 key 对应的数据太多，而相同 key 对应的数据都会发送到相同 的 reducer 上，从而导致内存不够。此时我们应该仔细分析这些异常的 key，很多情况下， 这些 key 对应的数据是异常数据，我们需要在 SQL 语句中进行过滤

2. 空 key 转换

   有时虽然某个 key 为空对应的数据很多，但是相应的数据不是异常数据，必须要包含在

   join 的结果中，此时我们可以表 a 中 key 为空的字段赋一个随机的值，使得数据随机均匀地 分不到不同的 reducer 上。

#### 9.3.3 MapJoin

#### 9.3.4 Group By

#### 9.3.5 count(distinct)去重

#### 9.3.6 笛卡尔积

#### 9.3.7 行列过滤

#### 9.3.8 动态分区调整

#### 9.3.9 分桶

#### 9.3.10 分区

### 9.4 数据倾斜

### 9.5 并行执行

### 9.6 严格模式

### 9.7 JVM 重用

### 9.8 推测执行

### 9.9 压缩

### 9.10 执行计划

