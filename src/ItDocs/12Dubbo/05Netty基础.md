# Netty基础

### 1. 什么是Netty？它解决了什么问题？

Netty是一个基于NIO的异步事件驱动的网络通信框架，提供了对TCP、UDP等协议的封装。它解决了原生Java NIO开发中以下痛点：

- **API复杂**：原生NIO需要开发者处理ByteBuffer、Selector、Channel等底层API，易出错
- **线程模型复杂**：需要自行设计Reactor线程模型，处理多路复用、线程安全等问题
- **粘包拆包**：TCP流式传输会导致数据边界问题，需要自行处理协议编解码
- **连接管理**：需要自行处理重连、心跳、空闲检测等连接管理逻辑
- **性能问题**：原生NIO的epoll空轮询bug等问题需要自行规避

Netty通过**ChannelHandler管道链**、**EventLoop线程模型**、**内置编解码器**等设计，让开发者专注于业务逻辑而非底层网络细节。

### 2. Reactor线程模型有哪几种？Netty使用的是哪种？

Reactor模型有三种经典变体：

- **单Reactor单线程**：一个Reactor线程同时负责接收新连接（accept）和处理IO读写事件。适用于高延迟、低负载场景，如Redis。缺点是单线程无法充分利用多核CPU，且处理慢时阻塞所有事件
- **单Reactor多线程**：一个Reactor线程负责接收连接和分发IO事件，Worker线程池负责业务逻辑处理。IO读写仍在Reactor线程完成，适合IO耗时短的场景
- **主从Reactor多线程**：**MainReactor**（Boss Group）负责接收新连接，将连接注册到 **SubReactor**（Worker Group）；SubReactor负责处理该连接上的IO读写事件。Acceptor和IO Handler分离，充分利用多核

Netty默认使用**主从Reactor多线程模型**：
- **Boss EventLoopGroup**：通常一个线程，负责处理Accept事件，接收连接后将Channel注册到Worker
- **Worker EventLoopGroup**：多个线程，每个EventLoop负责一组Channel的IO读写
- 一个 **EventLoop** 绑定一个**固定线程**和一个 **Selector**，一个Channel在其生命周期内绑定到同一个EventLoop，保证了Channel操作的**线程安全**

### 3. Netty的EventLoop和EventLoopGroup是如何工作的？

- **EventLoop**：本质是一个**死循环线程**，不断执行三个任务：1）轮询Selector上的IO事件；2）处理IO事件（如读就绪）；3）执行提交到该EventLoop的任务队列中的普通任务。一个EventLoop关联一个Selector和一个Thread
- **EventLoopGroup**：包含多个EventLoop，负责管理这些EventLoop的**分配策略**

**Channel与EventLoop的绑定关系**：
- 一个新连接接入时，Worker Group通过**轮询（Round-Robin）** 策略选择一个EventLoop
- Channel一旦注册到一个EventLoop，在其生命周期内**不会变更**
- 这种设计保证了同一个Channel上的所有IO事件和Handler执行都在**同一个线程**中，避免了加锁

### 4. Channel和Pipeline的关系是什么？

Netty中每个 **Channel** 有一个唯一的 **ChannelPipeline**，Pipeline中维护了一个**双向链表**结构的ChannelHandler链。

- **Channel**：代表一个网络连接（如Socket），负责底层IO操作（read/write/connect/bind）
- **ChannelPipeline**：拦截和处理IO事件的**责任链**，所有事件在执行链上按顺序传播
- **ChannelHandler**：真正的业务逻辑处理单元，分为 **ChannelInboundHandler**（处理入站事件如数据读取）和 **ChannelOutboundHandler**（处理出站事件如数据写出）

数据流向：
- **入站（Inbound）**：从Channel读取的数据 → Head Context → ... → Tail Context（从Head开始向后传播）
- **出站（Outbound）**：调用Channel.write() → Tail Context → ... → Head Context（从Tail开始向前传播）

Head Context负责底层的网络IO，Tail Context负责最终的兜底处理。

### 5. ChannelHandler的执行顺序是如何控制的？

Order：ChannelHandler的执行顺序由其**添加到Pipeline的顺序**决定：

- **InboundHandler**：按添加**正向顺序**执行（先添加的先执行）
- **OutboundHandler**：按添加**反向顺序**执行（后添加的先执行）

因为Inbound事件从Head→Tail传播，Outbound事件从Tail→Head传播。可以通过 **ChannelHandlerContext.fireChannelRead()** 手动控制事件是否向后传播，不调用则截断。

### 6. Netty如何解决TCP粘包和拆包问题？

TCP是流式协议，数据无边界。粘包指多个数据包合并为一个接收，拆包指一个数据包被拆分多次接收。

Netty提供了多种**内置编解码器**解决：

- **FixedLengthFrameDecoder**：按固定长度拆包，适合定长协议
- **LineBasedFrameDecoder**：按换行符 `\n` 或 `\r\n` 拆包，适合文本协议
- **DelimiterBasedFrameDecoder**：按自定义分隔符拆包
- **LengthFieldBasedFrameDecoder**：**最常用**，按消息头中的长度字段拆包，通用性强
- **HttpObjectDecoder**：HTTP协议的解码器

最通用方案：在消息头中写入消息体的**长度字段**，LengthFieldBasedFrameDecoder根据该字段正确切分数据包，解决粘包拆包问题。

### 7. Netty如何实现心跳机制和重连？

Netty通过 **IdleStateHandler** 实现空闲检测：

- 配置 **readerIdleTime**（读空闲）、**writerIdleTime**（写空闲）、**allIdleTime**（全部空闲）
- 指定时间未发生对应IO操作时，触发 **IdleStateEvent** 事件
- 在ChannelHandler的 `userEventTriggered()` 方法中处理该事件，发送心跳包

**客户端重连**：在ChannelFuture的 `channelInactive()` 回调中，使用 **EventLoop.schedule()** 定时发起重连，避免断线时频繁重连导致资源耗尽，通常加入指数退避策略。

### 8. 创建多少个线程合适？如何计算最佳线程数？

线程数的选择取决于任务的类型：

- **CPU密集型任务**（计算为主）：线程数 = **CPU核数 + 1**。+1是为了防止因缺页中断等导致CPU空闲。CPU密集型的核心瓶颈是CPU计算，线程过多反而引发上下文切换开销
- **IO密集型任务**（读写网络/磁盘为主）：最佳线程数 = **CPU核数 × (1 + IO耗时 / CPU耗时)**。IO密集型等待时间占比越高，需要的线程越多

**例题**：假设CPU核数为4核，IO耗时100ms，CPU耗时10ms，则最佳线程数 = 4 × (1 + 100/10) = 44个线程。IO耗时占比越大，可创建的线程越多——本质是**用更多线程来掩盖IO等待时间，提高CPU利用率**。

### 9. Netty的零拷贝机制是什么？

Netty的零拷贝指在数据传输过程中**减少或避免不必要的内存拷贝**，提升性能：

- **CompositeByteBuf**：将多个ByteBuf组合为逻辑上连续的缓冲区，避免物理合并拷贝
- **FileRegion**：基于FileChannel.transferTo()的**sendfile系统调用**，将文件数据直接从内核缓冲区传输到Socket缓冲区，**无需经过用户空间**
- **DirectBuffer**（直接内存）：使用堆外内存，数据在IO读写时无需从堆内拷贝到堆外

注意：Netty内部的零拷贝和操作系统的零拷贝（sendfile）有交叉但不等同，Netty还通过减少JVM内部的数据拷贝来实现零拷贝。

### 10. Netty的ByteBuf相比JDK ByteBuffer有哪些优势？

- **读写指针分离**：JDK ByteBuffer只有一个position指针，读写切换需要flip()；Netty ByteBuf有 **readerIndex** 和 **writerIndex** 两个独立指针，无需flip
- **动态扩容**：写入数据超出容量时**自动扩容**，JDK ByteBuffer固定大小，超界抛异常
- **池化技术**：通过 **PooledByteBufAllocator** 重用ByteBuf，减少GC压力
- **零拷贝支持**：提供CompositeByteBuf、slice、duplicate等零拷贝操作
- **引用计数**：通过referenceCount自动管理内存释放，防止内存泄漏
