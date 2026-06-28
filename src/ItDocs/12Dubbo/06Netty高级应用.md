# Netty高级应用

### 1. Netty内部执行流程是怎样的？（从bind到accept到read的完整链路）

Netty从启动到处理请求的完整流程如下：

**服务端启动流程：**
1. **创建Channel**：`ServerBootstrap.bind(port)` → `initAndRegister()` → `channelFactory.newChannel()` 创建 **NioServerSocketChannel** 实例，同时创建JDK底层的 **ServerSocketChannel** 并设置为**非阻塞模式**，内部实例化 **ChannelPipeline**（包含head和tail两个固定handler）
2. **初始化Channel**：`init(channel)` 向pipeline中添加 **ChannelInitializer** handler，其`initChannel()`方法会在注册时被调用，添加 **LoggingHandler** 和 **ServerBootstrapAcceptor**
3. **注册到Selector**：`AbstractUnsafe.register0()` → `AbstractNioChannel.doRegister()` → JDK `SelectionKey.ops = 0`（初始注册不关注任何事件）
4. **执行回调**：注册完成后回调 `ChannelInitializer.initChannel()`，此时LoggingHandler和ServerBootstrapAcceptor才真正加入Pipeline
5. **绑定端口**：`AbstractUnsafe.bind()` → JDK `ServerSocketChannel.bind(socketAddress)` → `pipeline.fireChannelActive()` → 在Head Context中调用 `readIfIsAutoRead()` → `AbstractNioChannel.doBeginRead()` → 设置 `selectionKey.interestOps(OP_ACCEPT)`，开始接收连接

**请求处理流程：**
1. **Accept**：NioEventLoop轮询到 **OP_ACCEPT** 事件 → `NioServerSocketChannel.unsafe.read()` → `pipeline.fireChannelRead(io.netty.channel.socket.nio.NioSocketChannel)` → **ServerBootstrapAcceptor** 接收，将NioSocketChannel注册到Worker Group的EventLoop上
2. **Read**：Worker EventLoop轮询到 **OP_READ** 事件 → `NioSocketChannel.unsafe.read()` → `pipeline.fireChannelRead(ByteBuf)` → 从头到尾依次经过 Pipeline 中的 InboundHandler 处理
3. **Write**：调用 `ChannelHandlerContext.writeAndFlush()` → 从尾到头反向经过 OutboundHandler → head context → 最终通过 JDK SocketChannel 写出

### 2. Netty的Channel是如何注册到EventLoop上的？

注册流程分为两步：**初次注册（服务端Channel）** 和 **连接注册（客户端Channel）**。

**初次注册流程：**
1. `AbstractUnsafe.register(eventLoop, promise)` 判断是否为 **inEventLoop**（当前线程是否是EventLoop绑定的线程）
2. 如果不是（启动时是main线程），将注册任务封装为Runnable通过 `eventLoop.execute()` 提交到EventLoop的任务队列
3. EventLoop线程从任务队列取出任务执行 → `register0(promise)` → `AbstractNioChannel.doRegister()` → 调用 **`javaChannel().register(eventLoop().selector, 0, this)`** 将JDK Channel注册到Selector上，**attachment** 设置为Netty Channel自身
4. 注册成功后触发 `pipeline.fireChannelRegistered()` → 传播Inbound事件
5. 如果Channel是Active状态，触发 `pipeline.fireChannelActive()` → 在head context中调用 `readIfIsAutoRead()` 设置 interestOps 开始监听IO事件

**连接注册流程：**
- ServerBootstrapAcceptor.accept() 接收到新连接后，调用 `childGroup.register(childChannel)`，采用 **Round-Robin** 策略从Worker Group中选择一个EventLoop
- 后续流程与初次注册相同，将NioSocketChannel注册到Worker EventLoop的Selector上

### 3. Netty的Future和Promise机制是如何设计的？

Netty在JDK Future基础上扩展了异步通知能力：

**Future接口（io.netty.util.concurrent.Future）**：
- 继承JDK `java.util.concurrent.Future`，增加 **sync()/await()** 阻塞方法
- 增加 **addListener()** 方法，任务完成时自动回调Listener，避免主动轮询
- sync()内部调用await()，但sync()在返回前会通过 **rethrowIfFailed()** 重新抛出异常

**Promise接口**：
- 继承Future，增加了 **setSuccess()/setFailure()** 等写入方法
- 使得Future既可以被**读取**（通过get()），也可以被**写入**（通过setSuccess()）

**DefaultPromise实现**：
- **result**：volatile修饰，保存执行结果
- **listeners**：GenericFutureListener链表，任务完成后回调
- **waiters**：等待线程数，用于唤醒控制
- setSuccess()/setFailure() → 设置result → notifyListeners() → 回调所有Listener → 唤醒所有等待线程
- 典型用法：创建一个DefaultPromise提交到EventLoop执行，完成后调用promise.setSuccess(result)

**ChannelPromise**：综合ChannelFuture和Promise，将Channel关联进来，用于IO操作的异步通知。

### 4. Netty中如何传输大文件？零拷贝sendfile是如何实现的？

大文件传输的核心技术是 **零拷贝**，避免将文件数据从内核空间拷贝到用户空间再拷贝回内核空间。

**传统文件传输（四次拷贝）**：
- 磁盘 → 内核缓冲区（DMA）→ 用户缓冲区（CPU）→ Socket缓冲区（CPU）→ 网卡（DMA）
- 涉及 **4次上下文切换**（read/write系统调用）和 **4次数据拷贝**

**sendfile零拷贝（两次拷贝）**：
- 磁盘 → 内核缓冲区（DMA）→ Socket缓冲区（CPU）→ 网卡（DMA）
- Linux 2.4+ 的sendfile支持 **DMA gather copy**，进一步减少为 **2次数据拷贝**（磁盘→内核缓冲区→网卡），且**无需经过用户空间**

**Netty中的实现**：
- **FileRegion**：底层调用 `FileChannel.transferTo()` → sendfile系统调用。使用方式：`ctx.write(new DefaultFileRegion(file, offset, length))`
- **ChunkedWriteHandler**：当大文件无法一次性发送时，将文件分块（chunk）传输，默认chunk大小为8192字节，支持自动暂停和恢复（利用Channel的writability状态）

**注意事项**：
- FileRegion不支持HTTPS（SSL加密需要操作明文数据，必须经过用户空间）
- FileRegion只能用于文件到网络的传输，不能用于文件到文件的拷贝
- HTTPS场景下使用 **ChunkedWriteHandler + ChunkedFile** 组合，数据会经过用户空间进行加密

### 5. 如何实现断点续传/断点上传？

断点续传基于HTTP协议的 **Range** 请求头实现：

**客户端流程：**
1. 发送HEAD请求获取文件大小，或通过响应头 `Content-Length` 获取总大小
2. 记录已下载位置，发送GET请求时携带 `Range: bytes=已下载位置-` 头
3. 服务端返回 `206 Partial Content` 和 `Content-Range: bytes start-end/total` 头
4. 从start位置开始继续接收数据，追加写入本地文件

**服务端实现（Netty）：**
```
// 解析Range头
String range = request.headers().get(HttpHeaderNames.RANGE);
// 格式: "bytes=0-100" 或 "bytes=100-"
long start = parseRangeStart(range);
long fileSize = randomAccessFile.length();
// 定位到断点位置
randomAccessFile.seek(start);
// 使用FileRegion传输
ctx.write(new DefaultFileRegion(randomAccessFile.getChannel(), start, fileSize - start));
// 设置响应头
response.headers().set(HttpHeaderNames.CONTENT_RANGE, "bytes " + start + "-" + (fileSize-1) + "/" + fileSize);
response.setStatus(HttpResponseStatus.PARTIAL_CONTENT);
```

**上传端断点续传：**
- 客户端先发送查询请求，获取服务端已接收的字节数
- 从已接收位置开始继续上传数据块，携带 `Content-Range` 头
- 服务端使用 `RandomAccessFile.seek(position)` 定位到断点位置继续写入
- 每块上传完成后记录进度，支持暂停和恢复

### 6. Netty拆包器LengthFieldBasedFrameDecoder的工作原理是什么？

**LengthFieldBasedFrameDecoder** 是Netty最通用的拆包器，根据消息头中的长度字段切分数据包。

**核心参数：**
- **maxFrameLength**：最大允许的帧长度
- **lengthFieldOffset**：长度字段在帧中的偏移量
- **lengthFieldLength**：长度字段的字节数（1/2/3/4/8）
- **lengthAdjustment**：长度字段值需要调整的值（补偿值）
- **initialBytesToStrip**：从帧中剥离的初始字节数（通常剥离长度字段本身）

**解码流程：**
```
// 核心方法 decode()
protected Object decode(ChannelHandlerContext ctx, ByteBuf in) {
    // 1. 判断可读字节是否足够读取长度字段
    if (in.readableBytes() < lengthFieldEndOffset) return null;
    
    // 2. 读取长度字段值
    int actualLengthFieldOffset = in.readerIndex() + lengthFieldOffset;
    long frameLength = getUnadjustedFrameLength(in, actualLengthFieldOffset, lengthFieldLength, byteOrder);
    
    // 3. 调整帧长度（加上调整值）
    frameLength += lengthAdjustment;
    
    // 4. 校验帧完整性
    if (in.readableBytes() < frameLength + lengthFieldEndOffset) return null;
    
    // 5. 剥离initialBytesToStrip字节，提取完整帧
    in.skipBytes(initialBytesToStrip);
    return in.readRetainedSlice(frameLength);
}
```

**常见配置举例：**
- 消息头2字节表示长度（不含长度字段本身）：`lengthFieldOffset=0, lengthFieldLength=2, lengthAdjustment=0, initialBytesToStrip=2`
- 消息头2字节表示长度（包含长度字段本身）：`lengthFieldOffset=0, lengthFieldLength=2, lengthAdjustment=-2, initialBytesToStrip=2`
- HTTP协议分块传输：HEADER包含 `Content-Length`，根据该值读取完整HTTP报文体

**FailFast机制**：当累积的数据超过maxFrameLength仍未构成完整帧时，立即抛出 **TooLongFrameException**，防止OOM。

### 7. Netty零拷贝的底层原理是什么？（操作系统级别）

Netty的零拷贝分为 **操作系统级别** 和 **JVM级别** 两个层面：

**操作系统级别的零拷贝：**
- **sendfile**：`FileChannel.transferTo()` → sendfile系统调用，数据从磁盘读入内核缓冲区后**直接通过DMA拷贝到网卡**，不经过用户空间。支持 **DMA gather** 的硬件可将内核缓冲区的数据直接聚合到网卡缓冲区
- **mmap（内存映射）**：`FileChannel.map()` 将文件映射到进程的**虚拟地址空间**，应用程序像操作内存一样读写文件，写操作由操作系统异步刷入磁盘。适用于频繁读写同一文件的小范围场景

**JVM级别的零拷贝（Netty特有）：**
- **DirectBuffer（堆外内存）**：使用 `PooledDirectByteBuf`，分配在**堆外（Direct Memory）**，IO操作时JVM直接将该内存地址传递给底层Native函数进行读写，**无需堆内→堆外拷贝**
- **CompositeByteBuf**：将多个ByteBuf**组合为一个逻辑视图**，不进行物理合并。适用于HTTP Chunked响应等需要拼接多个数据块的场景。通过 `addComponents()` 将多个ByteBuf的**指针/长度**组合到一个列表中，读取时按顺序遍历
- **ByteBuf.slice()/duplicate()**：基于**引用计数+共享底层内存**，slice()只共享部分数据（调整readerIndex/writerIndex），duplicate()共享全部数据

**性能对比**：
- 传统方式（4次拷贝）：文件16KB，耗时约 10-20us
- mmap（3次拷贝+缺页中断）：文件16KB，耗时约 3-5us（小文件优势明显）
- sendfile（2次拷贝）：文件16KB，耗时约 1-2us（大文件优势明显）
- sendfile + DMA gather（1次拷贝）：理论上最优

### 8. Netty源码中是如何解析HTTP协议的？

Netty通过 **HttpServerCodec**（双向编解码器）处理HTTP协议，其内部包含 **HttpRequestDecoder** 和 **HttpResponseEncoder**。

**核心组件：**
- **HttpServerCodec**：继承 `CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder>`，入站时用HttpRequestDecoder解码，出站时用HttpResponseEncoder编码
- **HttpObjectDecoder**：HTTP消息解码核心类，继承 `ByteToMessageDecoder`，采用**状态机模式**解析HTTP报文

**HttpObjectDecoder状态机：**
```
enum DecoderState {
    SKIP_CONTROL_CHARS,          // 跳过控制字符
    READ_INITIAL,                // 读取请求行: "GET /path HTTP/1.1"
    READ_HEADER,                 // 读取请求头: "Host: xxx"
    READ_VARIABLE_LENGTH_CONTENT, // 读取可变长内容（无Content-Length）
    READ_FIXED_LENGTH_CONTENT,   // 读取固定长内容（有Content-Length）
    READ_CHUNK_SIZE,             // 读取chunked块大小
    READ_CHUNKED_CONTENT,        // 读取chunked块内容
    READ_CHUNK_DELIMITER,        // 读取chunked块分隔符
    READ_CHUNK_FOOTER,           // 读取chunked尾部
    BAD_MESSAGE,                 // 非法消息
    UPGRADED                     // 协议升级
}
```

**HeaderParser解析细节：**
- 内部类 **HeaderParser** 实现 **ByteProcessor** 接口
- 核心方法 `forEachByte(this)` 逐字节遍历ByteBuf，**process(byte value)** 方法检测每个字节
- 遇到 `CR(\r)` 跳过不加入结果，遇到 `LF(\n)` 返回false结束当前行解析
- 读取的header行存入 **AppendableCharSequence**，返回给上层组合成HttpRequest对象

**HttpObjectAggregator**：
- 将分块到达的 **HttpRequest/HttpContent** 聚合成完整的 **FullHttpRequest**
- 配置 `maxContentLength` 限制消息体最大长度，防止OOM
- 收到 `100-continue` 期望时，如果消息体未超限自动回复 `100-continue` 响应

### 9. Netty的Pipeline事件传播机制是怎样的？

Pipeline事件传播基于**责任链模式**，每个ChannelHandlerContext是链表的一个节点。

**数据结构：**
- 双向链表：`head ↔ context1 ↔ context2 ↔ ... ↔ tail`
- each context包含 **next** 和 **prev** 指针
- head是**Inbound起始点**同时也是**Outbound终点**
- tail是**Outbound起始点**同时也是**Inbound终点**

**Inbound事件传播：**
- 调用 `pipeline.fireChannelRead(msg)` → `head.fireChannelRead(msg)` → head的 `invokeChannelRead()` → 调用head的handler处理 → 通过 `ctx.fireChannelRead(msg)` 传播给next
- 传播路径：`head → context1 → context2 → ... → tail`
- 不调用 `ctx.fireChannelRead()` 则**截断传播**，责任链终止

**Outbound事件传播：**
- 调用 `ctx.writeAndFlush(msg)` → 从当前context向前查找 **上一个OutboundHandler**
- 传播路径：从当前`context`向前 → `prev context` → ... → `head`
- head context负责真正的 **JDK Channel写操作**

**ChannelHandlerContext的作用：**
- 保存handler和pipeline的引用
- 提供fire方法用于向下传播事件
- 提供读写方法（read/write/writeAndFlush）
- 每个handler通过 **ctx.channel()** 获取所属Channel，通过 **ctx.pipeline()** 获取所属Pipeline
- 选择器功能：`ctx.executor().inEventLoop()` 判断当前线程是否为绑定的EventLoop线程

### 10. Netty的EventLoop是如何工作的？源码层面分析

EventLoop本质是一个 **死循环线程**，其核心逻辑在 `SingleThreadEventExecutor.Executor` 内部类和 `NioEventLoop.run()` 方法中。

**NioEventLoop.run() 循环：**
```
protected void run() {
    for (;;) {
        // 1. 策略选择：是否有任务需要执行
        int strategy = selectStrategy.calculateStrategy(selectSupplier, hasTasks());
        switch (strategy) {
            case SelectStrategy.SELECT: // 没有任务，阻塞select
                select(wakenUp.getAndSet(false));
                break;
            case SelectStrategy.CONTINUE: // 继续轮询
                continue;
        }
        
        // 2. 处理IO事件
        processSelectedKeys();
        
        // 3. 执行任务队列中的任务
        runAllTasks();
    }
}
```

**select阶段策略：**
- `hasTasks()` 返回true：如果没有阻塞任务则调用 `selectNow()` 非阻塞返回，否则执行 `select(timeoutMillis)` 阻塞等待
- `hasTasks()` 返回false：执行 `select()` 阻塞等待，直到被 **wakeUp** 唤醒
- 解决 **epoll空轮询bug**：记录select操作次数，如果连续 **SELECT_AUTO_RESTART_THRESHOLD（512次）** 没有IO事件，重建Selector

**processSelectedKeys：**
- 遍历selectedKeys，对每个SelectionKey：
  - `key.attachment()` 获取Netty的 **AbstractNioChannel**
  - 判断 `key.isAcceptable()` → `unsafe.read()` 处理新连接
  - 判断 `key.isReadable()` → `unsafe.read()` 读取数据
  - 判断 `key.isWritable()` → `unsafe.forceFlush()` 刷新缓冲区

**runAllTasks 阶段：**
- 从任务队列（**MpscQueue**，多生产者单消费者队列）中批量取出任务执行
- 支持 **延迟任务**（通过 `schedule(command, delay, unit)` 提交的任务存储在 **PriorityQueue** 中，按执行时间排序）
- 每次执行任务前检查运行时间，防止IO事件处理被无限延迟

**MpscQueue的作用**：允许外部线程（非EventLoop线程）安全地向EventLoop提交任务，采用无锁设计，避免多线程竞争。

### 11. Netty的ChannelInitializer是如何将自定义Handler加入Pipeline的？

ChannelInitializer是一个特殊的InboundHandler，用于**延迟添加**其他handler。

**工作原理：**
1. 在 `init(channel)` 阶段，将ChannelInitializer实例通过 `pipeline.addLast()` 添加到pipeline
2. Channel注册完成后，EventLoop触发 `pipeline.fireChannelRegistered()` → Inbound事件传播到ChannelInitializer时，调用 `handlerAdded()` 方法
3. `handlerAdded()` 内部调用 `initChannel(ch)` → 执行用户重写的 `initChannel()` 方法，在其中添加自定义handler
4. 所有handler添加完成后，ChannelInitializer调用 `ctx.pipeline().remove(this)` **将自己从pipeline中移除**

**典型示例：**
```
pipeline.addLast(new ChannelInitializer<SocketChannel>() {
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new StringDecoder());    // 添加解码器
        p.addLast(new StringEncoder());    // 添加编码器
        p.addLast(new BizHandler());       // 添加业务处理器
    }
});
```

注册完成后，pipeline状态变化：
- 注册前：`head ← ChannelInitializer → tail`
- 注册后initChannel执行完：`head ← StringDecoder ← StringEncoder ← BizHandler → tail`
- ChannelInitializer被移出自pipeline

这种设计保证了handler的初始化在EventLoop线程中执行，避免了多线程安全问题。
