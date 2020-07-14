# Hadoop 分布式集群搭建

## 一、集群规划

## 二、前置条件

### 2.1 虚拟机准备

准备克隆三台虚拟机，hadoop-11, hadoop-12, hadoop-13

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

### 2.3 

## 三、集群搭建
