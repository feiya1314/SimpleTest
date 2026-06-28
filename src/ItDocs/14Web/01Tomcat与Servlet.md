## 1. 请描述Tomcat的整体架构

Tomcat的核心组件是**连接器（Connector）和容器（Container）**。

一个**Tomcat只有一个Server**（一个JVM进程），Server可包含多个**Service**。每个Service包含多个Connector和一个Container。

**Connector**：负责处理连接相关的事情，提供Socket与Request/Response的转化。
- 监听端口（8080），接收TCP连接
- 解析HTTP/HTTPS/AJP协议
- 封装Request/Response对象
- 负责网络读写、协议解析、线程模型（BIO/NIO/NIO2/APR）

**Container（Servlet容器）**：封装和管理Servlet，处理Request请求。分为四个等级：
- **Engine（引擎）**：全局请求入口，所有请求的总调度，匹配域名分发请求
- **Host（虚拟主机）**：对应域名（localhost、www.xxx.com），一台Tomcat可配置多个域名站点
- **Context（Web应用）**：对应一个war/一个项目，同一个域名下可有多个Context，对应web.xml，独立类加载器、独立会话、隔离应用
- **Wrapper（包装器）**：包装Servlet，Context容器管理Wrapper

Container使用**Pipeline-Valve管道**处理请求，采用**责任链模式**。每个Pipeline都有唯一的**BaseValve**（不可删除），上层容器的BaseValve会调用下层容器的管道（StandardEngineValve → StandardHostValve → StandardContextValve → StandardWrapperValve）。

![Tomcat容器模型](../assets/14Web/3be84f5b964548a98d4d1f54d797c11a.png)

## 2. Tomcat处理一个HTTP请求的完整流程

1. 客户端发起HTTP请求，到达服务器8080端口
2. **Connector**接收TCP连接，分配线程，解析HTTP报文
3. Connector将请求封装成Request和Response
4. 请求交给Container的**Engine**总引擎
5. Engine根据域名匹配对应**Host虚拟主机**
6. Host根据项目路径匹配**Context当前应用**
7. Context根据URL匹配目标**Wrapper（Servlet）**
8. 调用Servlet的service() → doGet/doPost执行业务
9. 业务处理完毕，Response组装响应数据
10. Connector将结果写出到客户端，连接回收/关闭

**Connector内部组件**：
- **Endpoint**：处理底层Socket网络连接，实现TCP/IP协议
- **Processor**：将Endpoint接收的Socket封装成Request，实现HTTP协议
- **Adapter**：将Request适配到Servlet容器进行处理

Connector使用**ProtocolHandler**处理请求，不同的ProtocolHandler代表不同连接类型，如Http11Protocol（普通Socket）、Http11NioProtocol（NioSocket）。

## 3. Tomcat各容器如何通过Pipeline-Valve处理请求？

Pipeline-Valve是**责任链模式**，但有两个特点：
1. 每个Pipeline都有特定的**BaseValve**（不可删除），在管道最后一个执行
2. **上层容器的BaseValve会调用下层容器的管道**

处理流程：
1. Connector接收请求后调用EnginePipeline
2. 依次执行EngineValve1、EngineValve2...最后执行StandardEngineValve
3. StandardEngineValve中调用Host管道，依次执行HostValve，最后执行StandardHostValve
4. 依次调用Context管道和Wrapper管道，最后执行StandardWrapperValve
5. StandardWrapperValve中创建**FilterChain**，调用doFilter处理请求
6. FilterChain包含匹配的Filter和Servlet，依次调用所有Filter的doFilter和Servlet的service方法
7. 处理完将结果返回Connector，通过Socket返回客户端

## 4. Servlet容器是如何启动和初始化的？

Tomcat通过**Context容器**管理Servlet。一个Context对应一个Web工程，创建StandardContext容器时会设置url和path，并添加**ContextConfig**负责解析Web应用配置。

Tomcat启动基于**观察者模式**设计，所有容器继承Lifecycle接口，通过Listener通知状态变更。

**ContextConfig.init()**主要工作：
- 创建解析xml配置文件的contextDigester对象
- 读取默认context.xml、Host配置文件、Context自身配置文件
- 设置Context的DocBase

**Context容器的startInternal()**：
- 创建读取资源文件的对象和ClassLoader对象
- 设置应用的工作目录
- 启动辅助类（logger、realm、resources等）
- 修改启动状态，通知观察者
- 子容器初始化
- 获取ServletContext并设置必要参数
- 初始化load-on-startup的Servlet

**Web应用初始化**（ContextConfig.configureStart）：
- 解析web.xml文件（globalWebXml → hostWebXml → 应用WEB-INF/web.xml）
- 解析配置项保存到WebXml对象
- 如支持Servlet3.0，额外解析jar包中META-INF/web-fragment.xml以及对annotations的支持
- 将WebXml属性设置到Context容器，包括创建**Servlet对象、filter、listener**等

![Servlet启动时序图](../assets/14Web/12daad5920164c3491fb8b2f06ba9061.png)

## 5. Servlet是如何被创建和初始化的？

**创建Servlet实例**（Wrapper.loadServlet）：
- 如果load-on-startup配置项>0，Context容器启动时实例化
- 获取servletClass，交给InstanceManager创建对象
- 如果配置了jsp-file，servletClass为org.apache.jasper.servlet.JspServlet

**初始化Servlet**（StandardWrapper.initServlet）：
- 调用Servlet的init方法
- 将包装了StandardWrapper对象的**StandardWrapperFacade**作为ServletConfig传给Servlet

Servlet被包装成**StandardWrapper**而不是直接作为Servlet对象，是因为StandardWrapper是Tomcat容器的一部分，具有容器特征，而Servlet是独立Web开发标准，不应强耦合在Tomcat中。

![创建Servlet对象的类结构图](../assets/14Web/012e67040037404083079b2dec67b450.png)

## 6. 描述Servlet的体系结构和设计模式

Servlet规范围绕几个核心类运转：
- **ServletConfig**：获取Servlet配置属性，在Servlet init时由容器传递，实际是**StandardWrapperFacade**（门面模式）
- **ServletContext**：描述交易场景（应用上下文），实际是**ApplicationContextFacade**（门面模式）
- **ServletRequest / ServletResponse**：交互的具体对象

**门面模式（Facade）**被大量使用：RequestFacade、ResponseFacade、StandardWrapperFacade、ApplicationContextFacade，目的是封装容器内部数据，只暴露Servlet需要的内容。

**Request对象的创建过程**：
1. Tomcat接收请求后首先创建`org.apache.coyote.Request`和`org.apache.coyote.Response`（轻量级，快速分配给后续线程处理）
2. 交给用户线程时创建`org.apache.catalina.connector.Request`和`org.apache.catalina.connector.Response`
3. 传给Servlet时使用门面类**RequestFacade**和**ResponseFacade**

## 7. Servlet如何根据URL路由到具体处理类？

Tomcat使用`org.apache.tomcat.util.http.mapper`完成URL映射。Mapper保存了Container中所有子容器信息。

当Request进入Container容器之前，Mapper会根据请求的**hostname和contextpath**将host和context容器设置到Request的mappingData属性中，确定要访问的子容器。

**MapperListener**作为监听者加到Container的每个子容器中，只要容器发生变化就通知MapperListener更新mapper属性。

![Request在容器中的路由图](../assets/14Web/0b0f49b7c2164fa7bfe6d8846ef481b8.png)

## 8. Session在Tomcat中如何工作？

Session有三种工作方式：
1. **基于URL Path Parameter**（默认支持）：浏览器不支持Cookie时，SessionCookieName重写到URL参数中，默认SessionCookieName为"**JSESSIONID**"
2. **基于Cookie**（默认支持）
3. **基于SSL**（默认不支持，需connector.getAttribute("SSLEnabled")为TRUE）

注意：如果客户端支持Cookie，Tomcat会解析Cookie中的Session ID并覆盖URL中的Session ID。

有了Session ID，服务器端通过request.getSession()创建HttpSession对象（实际是**StandardSession的门面对象**），存入org.apache.catalina.Manager的sessions容器中。Manager管理所有Session生命周期：过期回收、服务器关闭时序列化到磁盘等。

## 9. Servlet中的Listener有哪些？

Servlet提供5种2类事件观察者接口：

**EventListeners类型**（4个）：
- ServletContextAttributeListener
- ServletRequestAttributeListener
- ServletRequestListener
- HttpSessionAttributeListener

**LifecycleListeners类型**（2个）：
- **ServletContextListener**
- **HttpSessionListener**

Listener实现类可配置在web.xml的\<listener\>标签中，也可动态添加。注意**ServletContextListener在容器启动后不能再添加**，因为其所监听的事件已不会再出现。

## 10. Tomcat的BIO和NIO模式有什么区别？

**BIO模式**：一个连接一个Worker线程。
- 客户端有连接时启动一个线程处理
- 如果连接不做任何事情（如keepalive等待），造成不必要的线程开销
- 线程资源有限（默认200），并发高时很快耗尽
- 读取socket并交给Worker线程的过程是**阻塞**的

**NIO模式**：基于Selector多路复用。
- 客户端连接注册到多路复用器**Selector**上
- 轮询到连接有I/O请求事件时才启动Worker线程处理
- 处理完毕Worker线程马上释放
- 读取socket并交给Worker线程的过程是**非阻塞**的

**为什么NIO能处理大量连接？** HTTP/1.1默认keep-alive为true，一个TCP连接在当前请求结束后不会立即释放。BIO模式下，等待期间Worker线程一直被占用，同时处理socket数目不能超过最大线程数。NIO基于IO多路复用，socket等待时不会占用Worker线程，可以处理远大于最大线程数的连接数。

## 11. Tomcat NIO模式下请求处理流程（Acceptor/Poller/Worker三线程模型）

Connector启动后启动三组线程：

**1. Acceptor线程组**：接受新连接
- 阻塞在ServerSocketChannel.accept()
- 有新连接时返回SocketChannel
- 配置Socket后封装到NioChannel，公平分配给Poller（轮询分配）
- 调用addEvent()将Socket添加到该Poller的事件队列

**2. Poller线程组**：监听Socket事件
- 使用Selector检测已就绪的Socket（默认最多2个）
- selector.select(1000)阻塞，所有Poller共用一个Selector（sun.nio.ch.EPollSelectorImpl）
- events()将事件队列中的Socket注册到EPollSelectorImpl
- 当Socket可读时，createSocketProcessor()封装为SocketProcessor（实现Runnable）
- execute(SocketProcessor)提交到Worker线程池的workQueue

**3. Worker线程组**：处理请求
- 从workQueue中取任务（阻塞在workQueue.take()）
- 调用SocketProcessor.run()处理Socket
- createProcessor()创建Http11Processor解析Socket，封装到org.apache.coyote.Request
- postParseRequest()处理映射关系（URL映射到Host、Context、Wrapper）
- CoyoteAdapter将org.apache.coyote.Request封装为org.apache.catalina.connector.Request
- connector.getService().getMapper().map()查询URL映射关系
- connector.getService().getContainer().getPipeline().getFirst().invoke()传递到Container处理

**默认配置**：
- Worker线程：初始10个，最多200个（可通过minSpareThreads和maxThreads配置）
- Poller：Math.min(2, Runtime.getRuntime().availableProcessors())，可通过pollerThreadCount配置
- Acceptor：默认1个，可通过acceptorThreadCount配置
- 连接等待队列长度：100（可通过acceptCount配置）

## 12. Tomcat如何实现应用隔离？（类加载器体系）

Tomcat自定义了类加载器来解决三个隔离问题：

**1. WebAppClassLoader（隔离不同Web应用）**
- 每个Context容器创建并维护一个WebAppClassLoader实例
- **不同的加载器实例加载的类被认为是不同的类**，即使类名相同
- 每个Web应用有自己的类空间，互不隔离

**2. SharedClassLoader（实现Web应用之间共享）**
- 作为WebAppClassLoader的父加载器
- 利用双亲委托机制，子加载器可通过父加载器加载类
- 共享类（如Spring的JAR包）放到SharedClassLoader的加载路径下

**3. CatalinaClassloader（隔离Tomcat自身类和Web应用类）**
- 通过兄弟关系实现隔离：CatalinaClassloader和SharedClassLoader平行，拥有同一个父加载器

**4. CommonClassLoader（Tomcat和各Web应用之间共享）**
- 作为CatalinaClassloader和SharedClassLoader的父加载器
- CommonClassLoader能加载的类都可被CatalinaClassLoader和SharedClassLoader使用

## 13. Spring在Tomcat中如何解决类加载问题？

Spring放在common或shared目录下由CommonClassLoader或SharedClassLoader加载，但用户的程序类在/WebApp/WEB-INF/下由WebAppClassLoader加载，不在CommonClassLoader的加载范围内。

Spring使用**线程上下文加载器（TCCL）**来解决：TCCL默认设置为**WebAppClassLoader**，哪个WebApp调用Spring，Spring就取该应用的WebAppClassLoader来加载Bean。

TCCL保存在线程私有数据里，同一线程后续执行中可以取出使用。Tomcat为每个Web应用创建WebAppClassLoader，在启动Web应用的线程里设置TCCL，Spring启动时将TCCL取出来加载Bean：

```java
cl = Thread.currentThread().getContextClassLoader();
```

## 14. Servlet 3.0 和 4.0 有什么区别？

| 特性 | Servlet 3.0 | Servlet 4.0 |
|------|-------------|-------------|
| **异步支持** | 引入异步处理支持，允许Servlet异步执行业务 | 进一步增强异步支持，引入异步I/O操作（非阻塞I/O） |
| **HTTP/2** | 不直接支持 | 支持HTTP/2（多路复用、头部压缩） |
| **WebSocket** | 支持HTTP Upgrade | WebSocket支持改进，更多特性 |
| **Trailer头部** | 不支持 | 支持HTTP/1.1 Trailer头部，可在请求/响应尾部包含附加信息 |
| **注解支持** | 引入注解支持 | 改进和扩展注解支持 |

## 15. 什么是Servlet异步请求？解决了什么问题？

**同步Servlet的问题**：
- Servlet线程在处理业务时（数据库操作、跨网络调用）一直处于**阻塞状态**
- 服务器工作线程数有限（默认200），Servlet线程被长期占用无法释放
- 并发请求大时，其他请求因获取不到连接而等待，影响服务器**吞吐量**

**异步Servlet的工作机制**：
1. 客户端发起请求
2. Servlet线程将请求转交给**异步线程**执行业务处理
3. Servlet线程本身返回到容器（可继续处理其他请求）
4. 异步线程处理完业务后直接生成响应数据（拥有ServletRequest和ServletResponse引用），或将请求转发给其他Servlet

**关键点**：
- 异步Servlet不阻塞容器接收和处理请求的Worker线程
- Worker线程节省了等待Servlet执行的时间，用来处理其他请求，提高吞吐量
- 适合Servlet业务耗时长的场景（尤其是IO密集型业务）
- **AsyncContext的目的是将HTTP thread和Worker thread解耦，提高Web容器的响应能力，不直接提升性能**
- 性能提升取决于Worker thread pool大小：Worker thread > HTTP thread时可能有提升

**AsyncContext.start()的注意事项**：
- Tomcat实际上利用HTTP thread pool来处理AsyncContext.start()
- 建议使用单独的ExecutorService（带线程池）处理AsyncContext
- Worker thread pool不一定需要比HTTP thread pool大，两者职责不同

## 16. Tomcat的keep-alive机制是什么？

HTTP/1.1默认keep-alive为true。一个TCP socket在当前请求结束后，如果没有新请求到来，不会立即释放，而是等timeout后再释放。

**BIO模式下**：socket等待期间Worker线程一直被占用，Tomcat可同时处理的socket数不能超过最大线程数。

**NIO模式下**：socket等待时不会占用Worker线程，基于IO多路复用可同时处理远大于最大线程数的连接。

关联参考：[Tomcat高效响应的秘密 keep-alive](https://zhuanlan.zhihu.com/p/20887507)

## 17. Nginx单点问题及扩容方案

**单机架构问题**：非高可用，web-server挂了整个系统就挂；扩展性差。

### 方案一：DNS轮询
多部署几份web-server，DNS层面域名每次解析到不同IP。
- 优点：零成本、部署简单、负载均衡
- 缺点：非高可用（DNS不保证IP对应服务是否可用）、扩容非实时（DNS有生效周期）、暴露太多外网IP

### 方案二：Nginx反向代理
增加反向代理层，利用Nginx将请求分发给多个web-server。
- 优点：DNS不用动、负载均衡、只暴露一个外网IP、扩容实时、能保证站点层可用性
- 缺点：**反向代理层成为单点**——Nginx挂了怎么办？

### 方案三：Keepalived + Nginx集群
两台Nginx部署Keepalived，设置相同虚IP（VIP）。
- Keepalived基于**VRRP协议**实现高可用
- 主服务器（MASTER）发送特定消息给备份服务器（BACKUP）
- 主服务器宕机时，备份服务器接管VIP继续提供服务
- 优点：解决高可用
- 缺点：资源利用率只有50%；Nginx仍是接入单点

### 方案四：LVS/F5
LVS（Linux Virtual Server）是四层交换器集群系统，根据目标地址和端口实现用户请求转发，本身不产生流量，是性能最好的负载均衡系统。F5是硬件负载均衡，性能更好。
- 通过LVS扩展多个Nginx
- 通过Keepalived+VIP保证可用性

### 方案五：LVS/F5 + DNS轮询
水平扩展（scale out）是解决性能问题的根本方案：通过DNS轮询线性扩展LVS层性能，通过Keepalived保证高可用，通过LVS扩展多个Nginx，通过Nginx做负载均衡和业务七层路由。

## 18. 四层和七层负载均衡有什么区别？

**四层负载均衡**（传输层）：
- 处理TCP/UDP协议
- 只将网络数据包转发到上游服务器，不检查数据包内容
- 通过检查TCP流中的前几个数据包做出有限路由决策
- 性能较高

**七层负载均衡**（应用层）：
- 处理HTTP协议
- 会截住网络流量并读取消息，根据URL或Cookie等做出负载均衡决策
- 与选定的上游服务器建立新TCP连接（或通过HTTP keepalives重用）
- 更耗CPU但可做出更智能的负载均衡决策
- 支持内容优化和更改（压缩、加密等）
- 慢速连接缓冲，提高性能
- 七层负载均衡设备通常称为"**反向代理服务器**"

## 19. JDBC Connection是线程安全的吗？数据库连接池呢？

**Connection不是线程安全的**。一个Connection对应底层一个TCP连接，多线程环境中使用同一个Connection会导致数据操作错乱，尤其有事务时（如线程A开启事务，线程B意外commit会导致数据错乱）。

**数据库连接池是线程安全的**（为多线程设计的）：
- 每个线程从池子申请一个Connection进行数据库操作时，**不允许另一个线程同时拿到这个Connection**
- 使用完毕必须归还连接池才能给下一个线程使用
- JDBC的对象Connection、Statement/PreparedStatement、ResultSet**都不是线程安全**，不能在多个线程中共享
