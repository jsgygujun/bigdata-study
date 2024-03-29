# MapReduce

## 一、MapReduce入门

### 1.1 MapReduce 定义

Mapreduce 是一个分布式运算程序的编程框架，是用户开发“基于 Hadoop 的数据分析应用”的核心框架。

Mapreduce 核心功能是将用户编写的业务逻辑代码和自带默认组件整合成一个完整的分布式运算程序，并发运行在一个 hadoop 集群上。

### 1.2 MapReduce优缺点

#### 1.2.1 优点

1. **易于编程**。它简单的实现一些接口，就可以完成一个分布式程序，这个 分布式程序可以分布到大量廉价的 PC 机器上运行。也就是说你写一个分布式程序，跟写一 个简单的串行程序是一模一样的。就是因为这个特点使得MapReduce编程变得非常流行。
2. **扩展性良好**。当你的计算资源不能得到满足的时候，你可以通过简单的增加机器 来扩展它的计算能力。
3. **容错性高**。MapReduce 设计的初衷就是使程序能够部署在廉价的 PC 机器上，这就要求它具有很高的容错性。比如其中一台机器挂了，它可以把上面的计算任务转移到另外一个节点上运行，不至于这个任务运行失败，而且这个过程不需要人工参与，而完全是由 Hadoop 内部完成的。
4. **适合 PB 级海量数据的离线处理**。它适合离线处理而不适合在线处理。比如像毫秒级别的返回一个结果，MapReduce 很难做到。

#### 1.2.2 缺点

MapReduce 不擅长做实时计算、流式计算、DAG(有向图)计算。

1. **实时计算**。MapReduce 无法像 Mysql 一样，在毫秒或者秒级内返回结果。
2. **流式计算**。流式计算的输入数据是动态的，而 MapReduce 的输入数据集是静态的，不能动态变化。这是因为 MapReduce 自身的设计特点决定了数据源必须是静态的。
3. **DAG（有向图）计算**。多个应用程序存在依赖关系，后一个应用程序的输入为前一个的输出。在这种情况下，MapReduce 并不是不能做，而是使用后，每个 MapReduce 作业的输出结果都会写入到磁盘，会造成大量的磁盘 IO，导致性能非常的低下。

### 1.3 MapReducce 核心思想

1. 分布式的运算程序往往需要分成至少 2 个阶段。
2. 第一个阶段的 MapTask 并发实例，完全并行运行，互不相干。
3. 第二个阶段的 ReduceTask 并发实例互不相干，但是他们的数据依赖于上一个阶段的所有 MapTask 并发实例的输出。
4. MapReduce 编程模型只能包含一个 map 阶段和一个 reduce 阶段，如果用户的业务逻辑非常复杂，那就只能多个 MapReduce 程序串行运行。

### 1.4 MapReduce 进程

一个完整的 MapReduce 程序在分布式运行时有三类实例进程:

1. **MrAppMaster**：负责整个程序的过程调度及状态协调。
2. **MapTask**：负责 map 阶段的整个数据处理流程。
3. **ReduceTask**：负责 reduce 阶段的整个数据处理流程。

### 1.5 MapReduce 编程规范

用户编写的程序分成三个部分： Mapper，Reducer，Driver（提交运行MR程序的客户端）

* Mapper阶段

  1. 用户自定义的 Mapper 类要继承 Mapper 类；
  2. Mapper的输入数据是 KV 对的形式（ KV 的类型可以自定义）；
  3. Mapper 中的业务逻辑写在 `map()` 方法中；
  4. Mapper 的输出数据是 KV 对的形式（ KV 的类型可自定义）；
  5. Maptask 进程对每一个 <K,V> 调用一次 `map()` 方法。

* Reducer阶段

  1. 用户自定义的 Reducer 类要继承 Mapper 类；
  2. Reducer 的输入数据类型对应 Mapper 的输出数据类型，也是 KV；
  3. Reducer 的业务逻辑写在 `reduce()` 方法中；
  4. ReduceTask 进程对每一组相同 K 的 <K,V> 组调用一次 `reduce()` 方法。

* Driver阶段

  整个程序需要一个 Driver 来进行提交，提交的是一个描述了各种必要信息的 job 对象

## 二、Hadoop 序列化

### 2.1 为什么要序列化？

一般来说，“活的”对象只生存在内存里，关机断电就没有了。而且“活的”对象只能由本地的进程使用，不能被发送到网络上的另外一台计算机。 然而序列化可以存储“活的” 对象，可以将“活的”对象发送到远程计算机。

### 2.2 什么是序列化和反序列化？

**序列化**就是把内存中的对象，转换成字节序列（或其他数据传输协议）以便于存储（持久化）和网络传输。

**反序列化**就是将收到字节序列（或其他数据传输协议）或者是硬盘的持久化数据，转换成内存中的对象。

### 2.3 为什么不使用 Java 序列化

Java 的序列化是一个重量级序列化框架，一个对象被序列化后，会附带很多额外的信息（各种校验信息，header，继承体系等），不便于在网络中高效传输。所以， Hadoop 自己开发了一套序列化机制，精简、高效。

### 2.4 为什么序列化对Hadoop很重要？

因为 Hadoop 在集群之间进行通讯或者 RPC 调用的时候，需要序列化，而且要求序列化要快，且体积要小，占用带宽要小。所以必须理解 Hadoop 的序列化机制。

序列化和反序列化在分布式数据处理领域经常出现：进程通信和永久存储。然而 Hadoop 中各个节点的通信是通过远程调用（RPC）实现的，那么 RPC 序列化要求具有以下特点:

1. **紧凑**：紧凑的格式能让我们充分利用网络带宽，而带宽是数据中心最稀缺的资源。
2. **快速**：进程通信形成了分布式系统的骨架，所以需要尽量减少序列化和反序列化的 性能开销，这是基本的。
3. **可扩展**： 协议为了满足新的需求变化，所以控制客户端和服务器过程中，需要直接引进相应的协议，这些是新协议，原序列化方式能支持新的协议报文。
4. **跨语言**：能支持不同语言写的客户端和服务端进行交互。

### 2.5 常用数据序列化类型

| Java 类型 | Hadoop Writable 类型 |
| --------- | -------------------- |
| boolean   | BooleanWritable      |
| byte      | ByteWritable         |
| int       | IntWritable          |
| float     | FloatWritable        |
| long      | LongWritable         |
| double    | DoubleWritable       |
| string    | Text                 |
| map       | MapWritable          |
| array     | ArrayWritable        |

## 三、MapReduce框架原理

### 3.1 MapReduce工作流程

![MapReduce详细工作流程1](/image/MapReduce详细工作流程1.png)
![MapReduce详细工作流程2](/image/MapReduce详细工作流程2.png)

上面的流程是整个 MapReduce 最全工作流程，但是 Shuffle 过程只是从第7步开始到第16步结束，具体 Shuffle 过程详解，如下：

1. MapTask 收集 `map()` 方法输出的 kv 对，放到环形内存缓冲区中。
2. 从环形内存缓冲区不断溢出（Spill）本地磁盘文件，可能会溢出多个文件。
3. 多个小的溢出文件会被合并成一个大的溢出文件。
4. 在溢出过程及合并的过程中，都要调用 Partitioner 进行分区和针对 key 进行排序。
5. ReduceTask 根据自己的分区号，去各个 MapTask 机器上取相应的分区数据。
6. ReduceTask 会取到同一个分区的来自不同 MapTask 的结果文件，ReduceTask 会将这些文件再进行合并（归并排序）。
7. 合并成一个大文件后，Shuffle 的过程也就结束了，后面进入 ReduceTask 的逻辑运算过程（从文件中取出一个一个的键值对 Group，调用用户自定义的 `reduce()`方法）

> Shuffle 中的环形缓冲区大小会影响 MapReduce 程序的执行效率，理论上来讲，缓冲区越大，磁盘 IO 的次数越少，执行速度也就越快。

### 3.2 InputFormat 数据输入

#### 3.2.1 切片与 MapTask 并行度决定机制

**问题**： MapTask 的并行度决定 Map 阶段的任务处理并发度，进而影响到整个 Job 的处理速度。

**MapTask 并行度决定机制**：

1. Map 阶段并行度由客户端在提交 Job 时的切片数决定。
2. 每一个 Split 切片分配一个 MapTask 并行实例处理。
3. 默认情况下，切片大小等于 BlockSize(128MB)。
4. 切片时不考虑数据集整体，而是逐个针对每一个文件单独切片。

#### 3.2.2 切片机制

* FileInputFormat 切片机制

  1. 切片机制

     1. 简单地按照文件的内容长度进行切片。
     2. 切片大小默认等于BlockSize(128M)。
     3. 切片时不考虑数据集整体，而是逐个针对每一个文件单独切片。

  2. 案例分析

     1. 输入

        ```shell
        file1.txt 320M
        file2.txt 10M
        ```

     2. 切片结果

        ```shell
        file1.txt.split1-- 0-128M
        file1.txt.split2-- 128-256M
        file1.txt.split3-- 256-320M
        file2.txt.split1-- 0-10M
        ```

* CombineTextInputFormat 切片机制

  框架默认的 TextInputFormat 切片机制是对任务按照文件规划切片，不管文件多小，都会是一个单独的切片，都会交给一个 MapTask，这样如果有大量小文件，就会产生大量的 MapTask，处理效率及其低下。

  CombineTextInputFormat 用于小文件过多的场景，它可以将多个小文件从逻辑上规划到一个切片中，这样，多个小文件就可以交给一个 MapTask 处理。

  1. 切片机制

     生成切片过程包括：**虚拟存储过程**和**切片过程**二部分。

     1. 虚拟存储过程

        1. 将输入目录下所有文件按照文件名称字典顺序一次读入，记录文件大小，并累加计算所有文件的总长度。

        2. 根据是否设置 setMaxInputSplitSize 值，将每个文件划分成一个一个 setMaxInputSplitSize 值大小的文件。

        3. 注意：当剩余数据大小超过 setMaxInputSplitSize 值且不大于 2 倍 setMaxInputSplitSize 值，此时将文件均分成 2 个等大的虚拟存储块（防止出现太小切片）。

           > 例如 setMaxInputSplitSize 值为 4M，最后文件剩余的大小为 4.02M，如果按照 4M 逻辑划分，就会出现 0.02M 的小的虚拟存储文件，所以将剩余的 4.02M 文件切分成 (2.01M 和 2.01M)两个文件。

     2. 切片过程

        1. 判断虚拟存储的文件大小是否大于 setMaxInputSplitSize 值，大于等于则单独形成一个切片。

        2. 如果不大于则跟下一个虚拟存储文件进行合并，共同形成一个切片。

           >测试举例：有 4 个小文件大小分别为 1.7M、5.1M、3.4M 以及 6.8M 这四个小文件，则虚拟存储之后形成 6 个文件块，大小分别为：1.7M，(2.55M、2.55M)，3.4M 以及(3.4M、3.4M)，最终会形成 3 个切片，大小分别为：(1.7+2.55)M，(2.55+3.4)M，(3.4+3.4)M

  2. 案例分析

### 3.3 MapTask工作机制
![MapTask工作机制](/image/MapTask工作机制.png)

1. Read 阶段

   MapTask 通过 RecordReader 从输入 InputSplit 中解析出一个一个的 Key/Value。

2. Map 阶段

   将解析出来的 Key/Value 交给用户编写的 `map()` 函数，并产生一系列新的 Key/Value

3. Collect 阶段

   用户编写的 `map()`函数中，当数据处理完成后，一般会调用`OutputCollector.collect()`输出结果。在该函数内部，它会将生成的 Key/Value 分区（通过调用Partitioner），并写入一个环形内存缓冲区中。

4. Spill 阶段

   即“溢写”阶段，当环形缓冲区满后，MapReduce 会将数据写到本地磁盘，生成一个临时文件。需要注意的是，将数据写入本地磁盘之前，先要对数据进行一次本地排序，并在必要时对数据进行合并、压缩等操作。

   Spill 阶段具体步骤：

   1. 利用快速排序算法对缓冲区内的数据进行排序，排序方式是，先按照分区编号 Partition 进行排序，然后按照 key 进行排序。这样，经过排序后，数据以分区为单位聚集在 一起，且同一分区内所有数据按照 key 有序。
   2. 按照分区编号由小到大依次将每个分区中的数据写入任务工作目录下的临时文件 output/spillN.out (N 表示当前溢写次数)中。如果用户设置了 Combiner，则写入文件之前，对每个分区中的数据进行一次聚集操作。
   3. 将分区数据的元信息写到内存索引数据结构 SpillRecord 中，其中每个分区的元信息包括在临时文件中的偏移量、压缩前数据大小和压缩后数据大小。如果当前内存索引大 小超过 1MB，则将内存索引写到文件 output/spillN.out.index 中。

5. Combine 阶段

   当所有数据处理完成后，MapTask 对所有临时文件进行一次合并， 以确保最终只会生成一个数据文件。

   当所有数据处理完后，MapTask 会将所有临时文件合并成一个大文件，并保存到文件 output/file.out 中，同时生成相应的索引文件 output/file.out.index。

   在进行文件合并过程中，MapTask 以分区为单位进行合并。对于某个分区，它将采用多 轮递归合并的方式。每轮合并 io.sort.factor(默认 10)个文件，并将产生的文件重新加入待 合并列表中，对文件排序后，重复以上过程，直到最终得到一个大文件。让每个 MapTask 最终只生成一个数据文件，可避免同时打开大量文件和同时读取大量小文件产生的随机读取带来的开销。

### 3.4 Shuffle机制

Mapreduce 确保每个 Reducer 的输入都是按 key 排序的。系统执行排序的过程（即将 Mapper 输出作为输入传给 Reducer）称为 Shuffle。

![Shuffle详细](/image/Shuffle详细机制.png)

### 3.5 ReduceTask工作机制

### 3.6 OutputFormat数据输出

### 3.7 Join多种应用

### 3.8 数据清洗（ETL）

1. 概述

   在运行核心业务 MapReduce 程序之前，往往要先对数据进行清洗，清理掉不符合用户要 求的数据。清理的过程往往只需要运行 mapper 程序，不需要运行 reduce 程序。

2. 案例： 日志清洗

### 3.9 计数器应用

Hadoop 为每个作业维护若干内置计数器，以描述多项指标。例如，某些计数器记录已处理的字节数和记录数，使用户可监控已处理的输入数据量和已产生的输出数据量。

1. API

   1. 采用枚举的方式统计计数

      ```java
      enum MyCounter{MALFORORMED,NORMAL}
      // 对枚举定义的自定义计数器加1
      context.getCounter(MyCounter.MALFORORMED).increment(1);
      ```

   2. 采用计数器组、计数器名称的方式统计

      ```java
      context.getCounter("counterGroup", "countera").increment(1);
      ```

      组名和计数器名称随便起，但最好有意义。

   3. 计数结果在程序运行后的控制台上查看。

2. 案例

### 3.10 MapReduce开发总结

在编写MapReduce程序时，需要考虑一下几个方面：

1. 输入数据接口： InputFormat
2. 逻辑处理接口： Mapper
3. Partitioner 分区
4. Comparable 排序
5. Combiner 合并
6. Reduce端分组： GroupingComparator
7. 逻辑处理接口： Reducer
8. 输出数据接口： OutputFormat

## 四、Hadoop数据压缩

## 五、Hadoop优化

### 5.1 MapReduce跑的慢的原因

Mapreduce 程序效率的瓶颈在于两点：

1. 计算机性能

   CPU、内存、磁盘、网络。

2. IO操作优化

   1. 数据倾斜
   2. map和reduce数设置不合理
   3. map运行时间太长，导致reduce等待过久
   4. 小文件过多
   5. 大量的不可分块的超大文件
   6. spill次数过多
   7. merge次数过多

### 5.2 MapReduce 优化方法

MapReduce 优化方法主要从七个方面考虑:数据输入、Map 阶段、Reduce 阶段、IO 传输、数据倾斜、小文件优化和常用的调优参数。

#### 5.2.1 数据输入

1. **合并小文件**：在执行 mr 任务前将小文件进行合并，大量的小文件会产生大量的 map 任务，增大 map 任务装载次数，而任务的装载比较耗时，从而导致 mr 运行较慢。
2. 采用 CombineTextInputFormat 来作为输入，解决输入端大量小文件场景。

#### 5.2.2 Map 阶段

1. **减少溢写(spill)次数**：通过调整 io.sort.mb 及 sort.spill.percent 参数值，增大触发 spill 的内存上限，减少 spill 次数，从而减少磁盘 IO。
2. **减少合并(merge)次数**：通过调整 io.sort.factor 参数，增大 merge 的文件数目，减少 merge 的次数，从而缩短 mr 处理时间。
3. 在 map 之后，不影响业务逻辑前提下，先进行 combine 处理，减少 I/O。

#### 5.2.3 Reduce 阶段

1. **合理设置 map 和 reduce 数**： 两个都不能设置太少，也不能设置太多。太少，会导致 task 等待，延长处理时间；太多，会导致 map、reduce 任务间竞争资源，造成处理超时等错误。
2. **设置 map、reduce 共存**： 调整 slowstart.completedmaps 参数，使 map 运行到一定程 度后，reduce 也开始运行，减少 reduce 的等待时间。
3. **规避使用 reduce**： 因为 reduce 在用于连接数据集的时候将会产生大量的网络消耗。
4. **合理设置 reduce 端的 buffer**： 默认情况下，数据达到一个阈值的时候，buffer 中的数据就会写入磁盘，然后 reduce 会从磁盘中获得所有的数据。也就是说，buffer 和 reduce是没有直接关联的，中间多个一个写磁盘->读磁盘的过程，既然有这个弊端，那么就可以通过参数来配置，使得 buffer 中的一部分数据可以直接输送到 reduce，从而减少 IO 开销：mapred.job.reduce.input.buffer.percent，默认为 0.0。当值大于 0 的时候，会保留指定比例的内存读 buffer 中的数据直接拿给 reduce 使用。这样一来，设置 buffer 需要内存，读取数据需要内存，reduce 计算也要内存，所以要根据作业的运行情况进行调整。

#### 5.2.4 IO 传输

1. **采用数据压缩的方式**： 减少网络 IO 的的时间。安装 Snappy 和 LZO 压缩编码器。
2. **使用 SequenceFile 二进制文件**

#### 5.2.5 数据倾斜问题

#### 5.2.6 小文件优化

#### 5.2.7 常用的调优参数

## 六、实战


