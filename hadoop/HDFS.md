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
   hadoop fs -moveFromLocal ./local_file /data/bigdata/new_dir
   ```

6. -appendToFile: 追加一个文件到已经存在的文件末尾

   ```shell
   hadoop fs -appendToFile ./local_file /data/bigdata/new_dir/remote_file
   ```

7. -cat: 显示文件内容

8. -tail: 显示一个文件的末尾

9. -chrgp/-chmod/-chown：与Linux系统中的用法一样，修改文件所属权限

10. -copyFromLocal: 从本地文件系统拷贝文件到HDFS目录

    ```shell
    hadoop fs -copyFromLocal README.md /data/bigdata/new_dir
    ```

11. -copyToLocal: 从HDFS拷贝到本地

    ```shell
    hadoop fs -copyToLocal /data/bigdata/new_dir/README.md ./
    ```

12. -cp: 从HDFS的一个路径拷贝到HDFS的另一个路径

    ```shell
    hadoop fs -cp /data/bigdata/old_dir/file.txt /data/bigdata/new_dir/file.txt
    ```

13. -mv: 在HDFS目录之间移动文件

    ```shell
    hadoop fs -mv /data/bigdata/old_dir/file.txt /data/bigdata/new_dir/file.txt
    ```

14. -get: 等同-copyToLocal，从HDFS下载文件到本地

    ```shell
    hadoop fs -get /data/bigdata/new_dir/file.txt ./
    ```

15. -getmerge： 合并下载多个文件，例如HDFS的目录/data/bigdata/aaa/目录下有文件log.1,log.2,...

    ```shell
    hadoop fs -getmerge /data/bigdata/aaa/* ./merged_log.txt
    ```

16. -put: 等同-copyFromLocal，从本地上传文件到HDFS

    ```shell
    hadoop fs -put ./README.md /data/bigdata/new_dir/
    ```

17. -rm: 删除文件或文件夹

    ```shell
    hadoop fs -rm /data/bigdata/new_dir/file.txt
    ```

18. -rmdir: 删除空目录

    ```shell
    hadoop fs -rmdir /data/bigdata/new_dir
    ```

19. -df: 统计文件系统的可用空间信息

    ```shell
    hadoop fs -df -h /
    ```

20. -du： 统计文件夹的大小信息

    ```shell
    hadoop fs -du -h /data/bigdata/new_dir
    hadoop fs -du -s -h /data/bigdata/new_dir
    ```

21. -setrep: 设置HDFS中文件的副本数量

    ```shell
    hadoop fs -setrep 2 /data/bigdata/new_dir/file.txt
    ```

    

    

    

    

    





