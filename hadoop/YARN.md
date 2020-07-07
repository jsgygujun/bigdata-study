# YARN

## 一、YARN 架构概述

1. ResourceManager(rm)： 处理客户端请求、启动并监控ApplicationMaster、监控NodeManager、资源分配与调度。
2. NodeManager(nm)： 单个节点上的资源管理、处理来自ResourceManager的命令、处理来自ApplicationMaster的命令。
3. ApplicationMaster: 数据切分、为应用程序申请资源并分配给内部任务、任务监控与容错。
4. Container： 对任务运行环境的抽象，封装了CPU、内存等多维资源以及环境变量、启动命令等任务运行相关的信息。