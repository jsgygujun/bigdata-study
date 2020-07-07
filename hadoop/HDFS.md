# HDFS

## 一、HDFS概述

### 1.1 HDFS产生背景

随着数据量越来越大，在一台机器上的磁盘装不下了，那么就分配到更多机器的磁盘中，但是不方便管理和维护，迫切需要一种系统来管理多台机器上的文件， 这就是分布式文件管理系统。HDFS 只是分布式文件管理系统中的一种。

### 1.2 HDFS概念

首先，HDFS是一个文件系统，用来存储文件，通过目录树来定位文件；其次，HDFS是分布式的，由多个服务器联合起来实现其功能，集群中的服务器有各自的角色。

1. NameNode(nn)： 存储文件的元数据，如文件名、目录结构、文件属性（生成时间、副本数、权限等）等，以及每个文件的块列表和块所在的DataNode等。
2. DataNode(dn)：在本地文件系统存储文件块数据，以及块数据的校验和。
3. Secondary NameNode(2nn)：用来监控HDFS状态的辅助后台程序，每隔一段时间获取HDFS元数据快照。

### 1.3 HDFS优缺点

#### 1.3.1 优点

1. 高容错性
   1. 数据自动保存多个副本。它通过增加副本的形式，提高容错性。
   2. 某一个副本丢失以后，它可以自动恢复。
2. 适合大数据处理
   1. 数据规模： 能够处理数据规模达到GB、TB、甚至PB级别的数据。
   2. 文件规模： 能够百万规模以上的文件数量，数量相当之大。
3. 流式数据访问
   1. 一次写入，多次读取，不能修改，只能追加。
   2. 它能保证数据的一致性。
4. 构建在廉价的机器上，通过多副本机制，提高可靠性。

#### 1.3.2 缺点

1. 不适合低延迟数据访问，比如毫秒级别的存取数据，是做不到的。
2. 无法高效的对大量小文件进行存储
   1. 存储大量小文件的话，会占用NameNode大量的内存来存储文件、目录和块信息，这样是不可取的，因为NameNode的内存总是有限的。
   2. 小文件存储的寻址时间会超过读时间，它违反了HDFS的设计目标。
3. 并发写入、文件随机修改
   1. 一个文件只能有一个写，不允许多个线程同时写。
   2. 仅支持文件追加，不支持文件的随机修改。

### 1.4 HDFS 架构

![HDFS架构图](/image/HDFS架构图.png)

这种结构主要由四个部分组成：HDSF Client、NameNode、DataNode和Secondary NameNode。
1. Client： 客户端
   1. 文件切分。文件在上传HDFS的时候，Client将文件切分称一个一个的Block，然后进行存储。
   2. 与NameNode交互，获取文件的位置信息。
   3. 与DataNode交互，读取或者写入数据。
   4. Client提供了一些命令来管理HDFS，比如启动或者关闭HDFS。
   5. Client可以通过一些命令来访问HDFS
2. NameNode: Master，管理者
   1. 管理HDFS的名称空间。
   2. 管理数据块(Block)映射信息。
   3. 配置副本策略。
   4. 处理客户端读写请求。
3. DataNode: Slave，NameNode下达命令，DataNode执行实际的操作。
   1. 存储实际的数据块。
   2. 执行数据块的读写操作。
4. Secondary NameNode: 并不是NameNode的热备。当NameNode挂掉的时候，它并不能马上替换NameNode并提供服务。
   1. 辅助NameNode，分担其工作量。
   2. 定期合并Fsimage和Edits，并推送给NameNode。
   3. 在紧急情况下，可辅助恢复NameNode。

## 二、HDFS命令行操作

基本语法： bin/hadoop fs 具体命令

常用命令实践：

1. 启动Hadoop集群

   ```shell
   sbin/start-dfs.sh
   sbin/start-yarn.sh
   ```

2. -help: 查找命令行参数

   ```shell
   hadoop fs -help rm
   ```

3. -ls: 显示目录信息

   ```shell
   hadoop fs -ls /
   ```

4. -mkdir: 创建目录

   ```shell
   hadoop fs -mkdir -p /data/bigdata/new_dir
   ```

5. -moveFromLocal: 从本地剪切粘贴到HDFS

   ```shell
   hadoop fs -moveFromLocal ./localfile /user/hadoop/hadoopfile
   ```

6. -appendToFile: 追加一个文件到已经存在的文件末尾

   ```shell
   hadoop fs -appendToFile ./localfile /user/hadoop/appendedfile
   ```

7. -cat: 显示文件内容

   ```shell
   hadoop fs -cat /user/hadoop/hadoopfile
   ```

8. -tail: 显示一个文件的末尾

   ```shell
   hadoop fs -cat /usr/hadoop/hadoopfile
   ```

9. -chrgp/-chmod/-chown：与Linux系统中的用法一样，修改文件所属权限

   ```shell
   hadoop fs -chgrp group /sur/hadoop/hadoopfile
   hadoop fs -chmod 777 /usr/hadoop/hadoopfile
   hadoop fs -chown hadoop:bigdata -R /usr/hadoop
   ```

10. -copyFromLocal: 从本地文件系统拷贝文件到HDFS目录

    ```shell
    hadoop fs -copyFromLocal localfile /usr/hadoop/hadoopfile
    hadoop fs -copyFromLocal -f localfile /usr/hadoop/hadoopfile # 如果hadoopfile已经存在，则内容将被覆盖
    ```

11. -copyToLocal: 从HDFS拷贝到本地

    ```shell
    hadoop fs -copyToLocal /usr/hadoop/hadoopfile ~/bigdata/localfile
    ```

12. -cp: 从HDFS的一个目录拷贝到HDFS的另一个目录

    ```shell
    hadoop fs -cp /usr/hadoop/dir1/file.txt /usr/hadoop/dir2/file.txt
    ```

13. -mv: 在HDFS目录之间移动文件

    ```shell
    hadoop fs -mv /usr/hadoop/dir1/file.txt /usr/hadoop/dir2/file.txt
    ```

14. -get: 等同-copyToLocal，从HDFS下载文件到本地

    ```shell
    hadoop fs -get /usr/hadoop/hadoopfile ~/bigdata/localfile
    ```

15. -getmerge： 合并下载多个文件，例如HDFS的目录/usr/hadoop/目录下有文件log.1,log.2,...

    ```shell
    hadoop fs -getmerge /usr/hadoop/* ~/bigdata/log.txt
    ```

16. -put: 等同-copyFromLocal，从本地上传文件到HDFS

    ```shell
    hadoop fs -put ~/bigdata/localfile /usr/hadoop/hadoopfile
    ```

17. -rm: 删除文件或文件夹

    ```shell
    hadoop fs -rm /usr/hadoop/hadoopfile
    hadoop fs -rm -r /usr/hadoop # -r 递归删除文件和子目录
    ```

18. -rmdir: 删除空目录

    ```shell
    hadoop fs -rmdir /usr/hadoop/data
    ```

19. -df: 统计文件系统的可用空间信息

    ```shell
    hadoop fs -df -h /  # -h 人类可读的大小信息，如64m
    ```

20. -du： 统计文件和文件夹的占用空间大小

    ```shell
    hadoop fs -du -h  /usr/hadoop/data # -h 人类可读的大小信息，如64m
    hadoop fs -du -s -h  /usr/hadoop/data # -s 文件或文件目录占用大小的汇总，若不指定则以深度为1递归计算文件和子目录占用空间大小信息。
    ```

21. -setrep: 设置HDFS中文件的副本数量

    ```shell
    hadoop fs -setrep 2 /usr/hadoop/hadoop.txt
    ```

## 三、HDFS 客户端操作



## 四、HDFS的数据流程

### 4.1 网络拓扑概念

#### 4.1.1 节点距离定义

在海量数据处理中，其主要限制因素是节点之间的传输速率——带宽很稀缺。这里的想法是将两个节点间的带宽作为距离衡量标准。

**节点距离**： 两个节点达到最近的公共祖先的距离总和。

例如，假设有数据中心d1机架r1中的节点n1。该节点可以表示为/d1/r1/n1。利用这种标记，这里给出四种距离描述：

* distance(/d1/r1/n1, /d1/r1/n1)=0: 同一节点上的进程
* distance(/d1/r1/n1, /d1/r1/n2)=2: 同一机架上的不同节点
* distance(/d1/r1/n1, /d1/r2/n1)=4: 同一数据中心不同机架上的节点
* distance(/d1/r1/n1, /d2/r1/n1)=6: 不同数据中心的节点

#### 4.1.2 副本节点选择

Hadoop2.7.2副本节点选择：

1. 第一个副本在Client所处的节点上。如果客户端在集群外，随机选择一个。
2. 第二个副本和第一个副本位于相同机架，随机节点。
3. 第三个副本位于不同机架，随机节点。

### 4.2 HDFS写数据流程

1. 客户端通过DFS(Distributed FileSystem)模块向NameNode请求上传文件，NameNode检查目标文件是否已经存在，父目录是否存在。
2. NameNode返回是否可以上传。
3. 客户端请求第一个Block上传到哪几个DataNode服务器上。
4. NameNode返回3个DataNode节点，分别为dn1,dn2,dn3。
5. 客户端通过FSDataOutputStream模块请求dn1上传数据，dn1收到请求会继续调用dn2，然后dn2调用dn3，将这个通信管道建立完成。
6. dn1,dn2,dn3逐级应答客户端。
7. 客户端开始往dn1上传第一个Block(先从磁盘读取数据放到本地内存缓存)，以packet为单位，dn1收到一个packet就会传给dn2，dn2传给dn3，dn1枚传一个packet会放入一个应答队列等待应答。
8. 当一个Block传输完成之后，客户端再次请求NameNode上传第二个Block的服务。（重复执行3-7）

### 4.3 HDFS读数据流程

1. 客户端通过DFS(Distributed FileSystem)模块向NameNode请求下载文件，NameNode通过查询元数据，找到文件块所在的DataNode地址。
2. 采用就近原则挑选一台DataNode服务器，请求读取数据。
3. DataNode开始传输给客户端（从磁盘里面读取数据输入流，以packet为单位做校验）
4. 客户端以packet为单位接收，先在本地缓存，然后写入目标文件。

## 五、NameNode工作机制



## 六、DataNode工作机制

















