
# **个人信息**
- 姓名 ：于飞 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;手机 ：13570845834    
- 学历 ：重庆大学/通信工程/本科  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Email ：yufei1313@gmail.com 
- 微信 ：13570845834 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;工作年限 ： 3.5年



# **工作经历**

## 中软公司 （ 2018年4月 ~ 至今 ）
主要在华为  SDS（结构化数据存储）中间件项目组工作
### **华为SDS lite&Service SDK开发**
项目介绍：SDS SDK 针对分布式 NoSql 数据库 Cassandra ，提供了统一操作接口，支持多 Data Center 容灾切换功能， 简化应用市场、相册等业务接入、降低使用成本。
我的职责：我主要负责 SDK 新需求的开发以及 bug 修复、优化等。例如接入配置中心、双云切换模块、SDK 去 spring 耦合等，通过这些方式有效的解决了业务侧维护大量配置的问题，方便了业务的接入和版本升级，同时通过支持双云主备切换功能，增强了业务容灾能力，接入 SDS 的业务数量也迅速提高。

### **Cassandra 集群拆分**
部分业务上线之初，由于成本及初期数据库量较少，急于上线，不同业务数据存储在同一个 SDS Cassandra 集群。业务数据成长起来之后，需要进行拆分到各自集群。
我主要负责 SDK 插件开发，用于将业务新增数据写入 MQ 中，存量数据导入新集群的 shell 脚本开发，以及用于同步新增数据到新集群的双写服务及数据一致性检查工具和修复工具开发。最后因为我对方案十分熟悉，由我全程协助业务进行集群拆分，最终在保证业务不停服，不影响业务当前服务的情况下，顺利完成了集群的拆分。
<br/>
### **华为SDS 异步 SDK开发**
当前 SDK 接口使用的都是同步接口，业务在使用时会阻塞业务线程，部分业务有使用异步接口的需求，为满足业务实际需要，需要提供一个支持异步的 SDK 。此项目我完成了接口开发、多 Data Center 切换模块开发。通过本项目开发，让我对异步场景及异步的使用更加了解。

### **Cassandra认证增强**
由于 Cassandra 默认的认证方式是客户端直接用户名密码明文编码发送到服务端，不符合华为安全要求，因此需要进行认证增强。本次使用 scram + sha256 方案进行认证增强。我负责整个认证的开发，包括前期分析 Cassandra 认证扩展、查看 Cassandra 认证部分相关源码、以及客户端和服务端的认证增强代码开发。最终完成开发，并达到华为安全要求，SDK 及 Cassandra 服务端顺利集成。

### **SDS 管理台项目开发**
SDS 管理台用于 Cassandra 集群部署、管理、定时修复任务，以及管理业务 SDS SDK 配置。我主要负责 Cassandra 定时修复任务以及 SDK 配置管理等功能开发。定时任务使用 Quartz 框架进行任务调度，运维可以通过管理台界面指定时间触发修复任务。SDK 配置存放在 Etcd 中，业务可以通过管理台界面进行配置管理。通过 SDS 管理台，大大简化了运维以及业务的工作，减少出错率。

### **其他项目**
负责 Cassandra 升级方案的验证以及升级 shell 脚本开发。Cassandra 容灾恢复 shell 脚本开发及测试。SDS SDK 云龙流水线搭建以及 SDS 其他各模块使用 maven 按照云眼规范打包，支持云眼自动化部署。通过这些脚本的开发，让我更加熟悉 Linux 下 shell 脚本编写。


## 四方精创公司 （ 2016年11月 ~ 2018年3月 ）

###      **WTS 项目**
WTS 项目是香港中国银行柜员系统，用于柜员处理贷款、开户等各项银行业务。我主要工作是业务开发，包括：检查需求书，与用户沟通需求；根据需求开发，包括页面的布局、数据校验，后端业务逻辑的实现、数据校验；使用测试数据验证流程是否正确；与主机组联合测试，确保在生产环境正常运行。

# **技能清单**

- Java 基础扎实，包括多线程、并发、IO、网络及 Servlet 等，熟悉 JVM 原理。
- 熟练使用和理解 spring、spring boot、springMVC、mybatis 等框架。
- 熟练使用及理解 Cassandra 分布式数据库和 Elasticsearch 分布式搜索引擎。
- 熟悉分布式相关原理，熟练使用 dubbo 分布式框架。
- 熟练使用 MySQL 等常用数据库
- 熟悉 Redis、kafka、zookeeper 等常用中间件。
- 熟练使用 Svn、Git 等版本管理工具
- 熟悉 Linux 常用命令，能使用 shell 编写脚本。
# **技能证书**

* **英语六级** 
* **c语言计算机二级**

# **自我评价**
为人乐观诚恳，对工作认证负责，工作中领导多次对我工作进行表扬。喜欢编程工作，热衷技术，平时也多在学习新的技术，紧跟技术发展，拥抱变化，拥抱创新。    
    
      