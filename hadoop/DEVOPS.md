# Hadoop 分布式集群搭建

## 一、集群规划

|      | Hadoop-11         | Hadoop-12                   | Hadoop-13          |
| ---- | ----------------- | --------------------------- | ------------------ |
| HDFS | NameNode/DataNode | DataNode                    | Secondary NameNode |
| YARN | NodeManager       | ResourceManager/NodeManager | NodeManager        |

## 二、前置条件

### 2.1 虚拟机准备

准备克隆三台虚拟机，hadoop-11, hadoop-12, hadoop-13，关闭防火墙，设置静态IP，设置主机名称

### 2.2 SSH 免密码登陆

将hadoop-11的公钥写到本机和 haoop-12,haoop-13 机器的`~/.ssh/authorized_key`文件中。

```shell
ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop-11
ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop-12
ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop-13
```

### 2.2 安装 xsync 集群分发脚本

脚本代码如下：
```shell
#!/bin/bash

pcount=$#
if((pcount==0)); then
  echo no args;
  exit;
fi

p1=$1
fname=`basename $p1`
echo fname=$fname

pdir=`cd -P $(dirname $p1); pwd`
echo pdir=$pdir

user=`whoami`

for ((host=12;host<14;host++)); do
  echo --------------- hadoop-$host --------------
  rsync -rvl $pdir/$fname $user@hadoop-$host:$pdir
done
```

### 2.3 安装 JDK 并配置环境

## 三、集群搭建

### 3.1 安装Hadoop 并配置环境变量

### 3.2 配置集群

集群配置文件在 `${HADOOP_HOME}/etc/hadoop` 目录下。

需要配置的文件如下：

* hadoop-env.sh

  指定 JDK 安装目录

  ```shell
  export JAVA_HOME=/opt/applications/jdk1.8.0_251
  ```

* core-site.xml

  1. 指定 HDFS 中 NameNode 的地址
  2. 指定 Hadoop 运行时产生临时文件的存储地址

  ```xml
  <configuration>
      <property>
          <!--指定 HDFS 中 NameNode 的地址-->
          <name>fs.defaultFS</name>
          <value>hdfs://hadoop-11:9000</value>
      </property>
      <property>
          <!--指定 Hadoop 运行时产生临时文件的存储目录-->
          <name>hadoop.tmp.dir</name>
          <value>/home/hadoop/tmp</value>
      </property>
  </configuration>
  ```

* hdfs-site.xml

  1. 指定 Secondary NameNode 地址
  2. 指定文件副本数
  3. 指定 NameNode 节点数据的存放位置（元数据）
  4. 指定 DataNode 节点数据的存放位置 （块数据）

  ```xml
  <property> 
      <!-- 指定 Secondary NameNode 地址 -->
    	<name>dfs.namenode.secondary.http-address</name> 
     	<value>hadoop-13:50090</value>
  </property>
  		<!-- 指定文件副本数 --> 
  <property>
  		<name>hadoop.tmp.dir</name>
  		<value>/opt/module/hadoop-2.7.2/data/tmp</value> 
  </property>
  <property>
      <!--指定 NameNode 节点数据的存放位置（元数据）-->
      <name>dfs.namenode.name.dir</name>
      <value>/home/hadoop/namenode/data</value>
  </property>
  <property>
        <!--指定 DataNode 节点数据的存放位置 （块数据）-->
      <name>dfs.datanode.data.dir</name>
      <value>/home/hadoop/datanode/data</value>
  </property>
  ```

* slaves

  ```xml
  hadoop-11
  hadoop-12
  hadoop-13
  ```

  

* yarn-env.sh

  指定 JDK 安装目录

* yarn-site.xml

  1. 指定Reducer获取数据的方式
  2. 指定 YARN 的 ResourceManager 的地址

  ```xml
  <property>
      <!-- reducer 获取数据的方式 --> 
  		<name>yarn.nodemanager.aux-services</name>
  		<value>mapreduce_shuffle</value> 
  </property>
  <property>
      <!-- 指定 YARN 的 ResourceManager 的地址 --> 
      <name>yarn.resourcemanager.hostname</name>
      <value>hadoop-12</value> 
  </property>
  ```

* apped-env.sh

  指定 JDK 安装目录

* mapped-site.xml

  指定 MapReduce 运行在 YARN 上

  ```xml
  <!-- 指定 mr 运行在 yarn 上 -->
  <property>
      <name>mapreduce.framework.name</name>
      <value>yarn</value>
  </property>
  ```

## 四、集群启动

### 4.1 启动 HDFS 

如果是第一次启动 HDFS，则需要格式化 NameNode.

```shll
hdfs namenode -format
```

启动 HDFS 集群，必须要 NameNode 节点上启动

```shell
start-dfs.sh
```

停止 HDFS 集群

```shll
stop-dfs.sh
```

### 4.2 启动 YARN

启动 YARN 集群，必须在 ResourceManager 节点上启动

```shll
start-yarn.sh
```

停止 YARN 集群

```shell
stop-yarn.sh
```

