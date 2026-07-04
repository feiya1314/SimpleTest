# 1. CompletableFuture概述

CompletableFuture是什么？和传统Future有什么区别？

**定义：**

CompletableFuture = **Future + 回调 + 任务编排**，JDK 8引入，实现了Future和CompletionStage接口，提供非阻塞的异步编程能力。

**与Future的本质区别：**

| 特性 | Future | CompletableFuture |
|------|--------|-------------------|
| 获取结果 | get()阻塞 | 回调非阻塞、链式触发 |
| 任务编排 | 不支持 | 串行、并行、聚合组合 |
| 异常处理 | 难捕获 | 自动透传、可全局捕获 |
| 编程模型 | 被动阻塞 | 事件驱动、流式异步 |

**核心能力：**

- **非阻塞回调**：任务完成自动触发后续逻辑，无需手动get()
- **链式调用**：多个异步操作串联编排
- **多任务组合**：等待所有完成或任意一个完成
- **异常自动传递**：异常沿回调链自动透传

# 2. 核心底层结构

CompletableFuture的底层是如何实现的？

内部基于 **volatile状态机 + 结果缓存 + 回调链表** 实现，异步任务交由ForkJoin公共线程池执行。

**1. 内部状态机**

内部有一个 volatile int 状态，取值包括：
- 未完成
- 正常完成
- 异常完成
- 取消

任何任务完成或异常，都会通过 **CAS修改状态**。

**2. 结果载体**

存储正常返回值或异常信息，任务完成后写入，回调链读取。

**3. 等待栈 / 回调链表（核心）**

当任务还没完成时，**后续依赖的回调任务（thenApply/thenAccept/thenRun）不会阻塞线程**，而是封装成 **Completion节点**，挂到当前Future的**回调链表/栈**上；等前面任务一完成，**主动唤醒、串行触发后续所有回调**。

**4. 默认线程池**

没有指定Executor时，用 **ForkJoinPool.commonPool()**；异步任务交给公共池线程执行，回调也复用池线程。


# 3. 核心工作原理

CompletableFuture的任务执行、回调挂载和异常传播是怎么工作的？

**1. 异步任务执行**

supplyAsync / runAsync 把任务提交到 **ForkJoin公共线程池**，子线程执行业务逻辑，执行完 **CAS修改状态为完成**，写入结果或异常。

**2. 回调任务挂载（关键）**

调用 thenApply / thenAccept / thenCombine 时：
- 如果 **前面任务已完成**：立刻直接执行回调
- 如果 **前面还没完成**：把回调函数包装成 **依赖节点**，**挂到当前CompletableFuture的回调链表上**，**当前线程不阻塞，直接返回**

**3. 任务完成触发回调**

前面任务结束 → 修改状态 → **遍历链表，逐个触发后续回调任务**，形成 **链式自动流转**，全程 **无阻塞、轮询、自旋**。不靠 wait/notify，靠 **状态机 + 回调链表**，事件驱动模型。

**4. 异常传播机制**

一旦任务抛出异常：
- 状态标记为 **异常完成**
- 异常自动向后传递给所有链式回调
- 可通过 exceptionally / handle 捕获处理
- 不处理也不会丢异常，会一直透传

**5. 链式编排实现**

每一个 thenXxx 都会 **生成一个新的CompletableFuture**，前后形成依赖链；前一个完成 → 驱动后一个执行，串行/并行/组合都基于这个依赖链。


# 4. 常用API：异步任务创建与回调

CompletableFuture的常用API有哪些？怎么用？

**创建异步任务：**

```java
// runAsync: 无返回值
CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
    System.out.println("异步执行");
});

// supplyAsync: 有返回值
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "result";
});

// 指定自定义线程池
ExecutorService pool = Executors.newFixedThreadPool(10);
CompletableFuture.supplyAsync(() -> "result", pool);
```

**回调方法：**

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "hello");

// thenApply: 转换结果（有返回值）
CompletableFuture<String> result = future.thenApply(s -> s + " world");

// thenAccept: 消费结果（无返回值）
future.thenAccept(s -> System.out.println(s));

// thenRun: 任务完成后执行（不关心结果）
future.thenRun(() -> System.out.println("done"));

// 指定Executor执行回调
future.thenApplyAsync(s -> s + " world", executor);
```


# 5. 异常处理

CompletableFuture的异常怎么处理？exceptionally、whenComplete、handle有什么区别？

**exceptionally：** 只在抛出异常时触发，类似 try-catch 中的 catch，可以捕获异常并返回默认值。

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    if (true) throw new RuntimeException("error");
    return "success";
}).exceptionally(e -> {
    System.out.println("捕获异常: " + e.getMessage());
    return "default";  // 返回默认值
});
```

**whenComplete：** 不管是否异常都会执行，类似 try-catch-finally 中的 finally，不改变结果。

```java
CompletableFuture.supplyAsync(() -> "success")
    .whenComplete((result, ex) -> {
        if (ex != null) {
            System.out.println("异常: " + ex.getMessage());
        } else {
            System.out.println("结果: " + result);
        }
    });
```

**handle：** 不管是否异常都会执行，且可以在一个方法里对正常结果转换或对异常捕获处理，类似 whenComplete + thenApply 的结合。

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    if (true) throw new RuntimeException("error");
    return "success";
}).handle((result, ex) -> {
    if (ex != null) {
        return "处理异常后的默认值";
    }
    return result;
});
```

**三种方法对比：**

| 方法 | 触发条件 | 能否改变结果 | 类比 |
|------|---------|-------------|------|
| exceptionally | 仅异常时 | 能 | catch |
| whenComplete | 正常/异常都触发 | 不能 | finally |
| handle | 正常/异常都触发 | 能 | whenComplete+thenApply |


# 6. 任务组合

CompletableFuture如何实现多个异步任务组合？thenCombine、allOf、anyOf怎么用？

**thenCombine：合并两个异步任务的结果**

```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "hello");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "world");

// 两个任务都完成，合并结果
CompletableFuture<String> result = f1.thenCombine(f2, (a, b) -> a + " " + b);
// 结果: "hello world"
```

**allOf：等待所有任务完成**

```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "a");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "b");
CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "c");

// 等待全部完成
CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2, f3);
all.thenRun(() -> System.out.println("全部完成"));

// 收集所有结果
CompletableFuture<List<String>> results = all.thenApply(v ->
    Stream.of(f1, f2, f3)
        .map(CompletableFuture::join)
        .collect(Collectors.toList())
);
```

**anyOf：任意一个任务完成即触发**

```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
    Thread.sleep(2000); return "slow";
});
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
    return "fast";
});

// 任意一个完成即返回
CompletableFuture<Object> result = CompletableFuture.anyOf(f1, f2);
// 结果: "fast"（先完成的那个）
```


# 7. 使用建议与实践

使用CompletableFuture需要注意什么？

**1. 接口返回约定**

方法返回 CompletableFuture，调用者一眼就知道这是异步API，也清楚如何调用它——这是一种约定。

**2. 合理使用线程池**

- 默认用 ForkJoinPool.commonPool()，所有并行任务共享
- CPU密集任务适合commonPool，IO密集任务建议自定义线程池
- 用 supplyAsync(task, executor) 解耦，避免互相影响

**3. 异常处理不可省略**

- 异步链路中任何环节抛出异常，会沿链传播
- 链路末端必须用 exceptionally / handle 兜底
- 不处理异常会静默消失（没有线程可以抛）

**4. 注意回调线程**

回调任务默认复用前面完成的线程或公共池线程。用 thenApplyAsync 可以指定独立线程池。

**5. 适用场景**

- 多个独立IO操作并行（如查多个接口、查多家酒店价格）
- 同步转异步，配合Lambda几句话完成
- 协调多个异步操作完成后合并结果
- 等待多个异步操作中最快的一个完成

**6. 不适用场景**

- 简单同步操作（不需要异步的开销）
- CPU密集且无任务编排需求（直接用并行流更简单）
- 需要取消任务（CompletableFuture的取消是"打断标记"并非真正中断线程）
