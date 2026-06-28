# RPC通信

### 1. Dubbo中本地暴露和远程暴露有什么区别？

Dubbo中一个服务可能既是Provider又是Consumer，存在自己调用自己的情况，如果再通过网络去访问是舍近求远，因此Dubbo设计了本地暴露机制。

- **本地暴露**：服务暴露在**JVM内部**，调用时不走网络通信，直接通过本地引用调用。适用于服务自调用场景
- **远程暴露**：将服务的**IP、端口等信息**暴露给远程客户端，调用时需要经过网络通信（序列化→传输→反序列化）

Dubbo在服务导出时，会**同时进行本地暴露和远程暴露**：
- `ServiceConfig.export()` 方法中，如果配置了本地暴露（`scope=local` 或未显式配置 `scope=remote`），会先调用 `Invoker.export()` 导出本地服务
- 本地暴露使用 **`InjvmProtocol`**，将服务注册到 `injvm://` 协议的URL上
- 远程暴露使用配置的协议（如 `dubbo://`），将服务注册到注册中心

### 2. 什么是RPC异步调用？Dubbo如何实现异步调用？

**同步调用**：客户端发起调用后**阻塞等待**服务端返回结果，期间线程被挂起。

**异步调用**：客户端发起调用后**立即返回**，不等待结果。可以通过两种方式获取结果：
- **Callback回调**：服务端处理完成后回调客户端注册的回调函数
- **Future模式**：调用返回一个Future对象，客户端在需要结果时通过 `future.get()` 阻塞或轮询获取

Dubbo通过**信息交换层（Exchange层）**实现**同步转异步**：

1. 客户端发起RPC调用时，Exchange层将请求封装为 **Request** 对象，并创建一个 **DefaultFuture**（实现了 `CompletableFuture`）
2. 每个 Request 分配唯一 ID，DefaultFuture 以ID为Key存入 `FUTURES` 静态Map中
3. 客户端线程立即返回 DefaultFuture，无需阻塞等待
4. 在同步调用场景中，上层Transport层通过 `future.get(timeout)` **阻塞等待**结果，实现"同步调用"的编程模型
5. 在异步调用场景中，客户端可以直接对 DefaultFuture 设置回调，或者通过 `RpcContext.getFuture()` 获取 Future 后自行处理

**Dubbo异步调用的使用方式**（从2.0.7开始支持）：
- 配置 `async=true` 开启异步调用
- 调用后通过 `RpcContext.getContext().getFuture()` 获取 Future
- 通过 `future.get()` 或 `future.whenComplete()` 获取结果

### 3. 服务端如何将RPC调用结果异步返回给客户端？

服务端通过 **Netty + DefaultFuture** 机制实现异步结果返回，核心流程如下：

1. **请求处理**：服务端 Netty 接收到请求后，交由 **DubboProtocol.requestHandler** 处理，通过线程池异步执行业务逻辑
2. **结果封装**：业务方法执行完成后，结果被封装为 **Response** 对象，包含与 Request 对应的 **mID（消息ID）**
3. **通道写出**：通过 Netty 的 **ChannelHandlerContext.writeAndFlush()** 将 Response 写出到网络通道
4. **客户端接收**：客户端 Netty 收到 Response 后，根据 mID 从 `FUTURES` Map 中查找对应的 **DefaultFuture**
5. **结果通知**：调用 `DefaultFuture.doReceived(Response)`，将结果设置到 Future 中，唤醒等待的客户端线程或触发回调

**关键设计点：**
- Response **不需要序列化Future本身**，只需序列化**调用结果**（业务方法的返回值）
- Request和Response通过 **唯一消息ID** 关联，服务端处理完成后只需将结果连同消息ID一起返回，客户端根据消息ID匹配对应的Future
- 整个过程中**没有任何Future被序列化传输**，Future只存在于客户端内存中，服务端返回的是真正的业务结果

### 4. 如何实现一个简单的异步RPC通信？

基于Netty实现异步RPC通信的核心组件：

1. **客户端**：
   - 创建 Netty **Bootstrap**，连接服务端
   - 调用远程方法时，生成**唯一请求ID**，创建 **RpcFuture** 并存入Map
   - 将请求（含请求ID、方法名、参数）序列化后通过 Channel 发送
   - 返回 RpcFuture 给调用方

2. **服务端**：
   - 创建 Netty **ServerBootstrap**，绑定端口
   - 接收请求后反序列化，根据方法名和参数反射调用本地服务
   - 将结果（含请求ID）序列化后写回 Channel

3. **结果回调**：
   - 客户端 Netty Handler 收到响应后，根据请求ID从Map中取出 RpcFuture
   - 调用 `future.setSuccess(result)` 或 `future.setFailure(exception)`
   - 调用方通过 `future.get()` 阻塞获取或注册回调异步获取

**核心数据结构**：`Map<Long, RpcFuture> PENDING` 用于关联请求和响应，每个请求发送前生成唯一ID并存入Map，收到响应后根据ID取出对应的Future并写入结果。
