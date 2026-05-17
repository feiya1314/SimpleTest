# Synchronized

### 1. synchronized的底层实现原理

synchronized的底层实现原理是什么？它是如何实现锁的？

**原理分析**

**核心组件：**

1. **Monitor（管程/监视器锁）**：每个对象有一个关联的Monitor
2. **ObjectMonitor**：HotSpot中Monitor的实现，C++对象
3. **MonitorEnter/MonitorExit**：字节码层面的指令
4. **对象头（Mark Word）**：存储锁状态信息

**字节码层面：**

```java
public void syncMethod() {
    synchronized (this) {
        // 业务逻辑
    }
}

// 编译后的字节码
monitorenter    // 获取锁
// 业务逻辑
monitorexit     // 释放锁
```

**对象头结构（64位）：**

| 锁状态 | Mark Word结构 |
|-------|--------------|
| 无锁 | 25位对象哈希 + 4位年龄 + 1位偏向锁位 + 2位锁标志位(01) |
| 偏向锁 | 23位线程ID + 2位epoch + 4位年龄 + 1位偏向锁位 + 2位锁标志位(01) |
| 轻量级锁 | 62位指针指向栈中锁记录 + 2位锁标志位(00) |
| 重量级锁 | 62位指针指向ObjectMonitor + 2位锁标志位(10) |

**Monitor工作流程：**

```
线程竞争synchronized锁
    ↓
检查对象头锁状态
    ↓
无锁/偏向锁 → 尝试CAS修改对象头
    ↓
成功 → 获取锁
    ↓
失败 → 膨胀为轻量级锁/重量级锁
    ↓
重量级锁：ObjectMonitor._WaitSet阻塞
```

> 为什么synchronized不需要CAS但ReentrantLock需要？

synchronized是JVM内置锁，由JVM实现。ReentrantLock是JDK提供的显式锁，基于AQS的CAS实现。synchronized在锁升级过程中也使用CAS（如修改对象头）。

---

### 2. synchronized的锁升级过程

synchronized的锁升级过程是怎样的？为什么不能降级？

**原理分析**

**锁升级方向：**

```
偏向锁 → 轻量级锁 → 重量级锁
  ↑          ↑          ↑
  ↓          ↓          ↓
不可逆     不可逆      不可逆
```

**偏向锁（Biased Locking）：**

- 目的：消除无竞争下的同步开销
- 原理：记录线程ID到对象头，后续该线程进入同步块无需任何同步操作
- 条件：-XX:+UseBiasedLocking（JDK 15默认禁用）

**轻量级锁（Lightweight Locking）：**

- 目的：基于CAS的"自旋"避免线程阻塞
- 原理：在栈帧中创建Lock Record，通过CAS将Mark Word复制到栈帧

**重量级锁（Heavyweight Locking）：**

- 目的：适用于多线程竞争场景
- 原理：通过ObjectMonitor的_WaitSet和_EntryList进行阻塞等待

**为什么不能降级？**

1. **性能考虑**：降级需要额外开销，且可能再次升级
2. **实现复杂性**：降级涉及复杂的状态判断
3. **HotSpot设计**：采用"一旦升级就不再降级"策略，避免额外复杂性

> 为什么偏向锁在JDK 15后默认禁用？

因为现代应用通常使用轻量级锁，且偏向锁会带来额外开销：
- 撤销成本高（需在安全点STW）
- 实际使用中偏向锁经常成为性能瓶颈

---

### 3. synchronized与ReentrantLock的区别

synchronized和ReentrantLock有什么区别？各自的适用场景是什么？

**原理分析**

**区别对比：**

| 特性 | synchronized | ReentrantLock |
|-----|-------------|---------------|
| 实现 | JVM内置 | JDK API |
| 锁获取 | 自动获取/释放 | 手动lock/unlock |
| 公平锁 | 不支持 | 支持（构造参数） |
| 尝试获取 | 不支持 | **tryLock()** |
| 超时获取 | 不支持 | **tryLock(long time)** |
| 中断获取 | 不支持 | **lockInterruptibly()** |
| 条件变量 | 内置（wait/notify） | **Condition** |
| 锁状态检查 | 无法检查 | **isLocked()** |
| 性能 | JDK6+优化后相近 | 略有优势 |

**synchronized优势：**

```java
// 自动释放锁
synchronized (lock) {
    // 业务逻辑
    // 即使抛出异常，锁也会自动释放
}
```

**ReentrantLock优势：**

```java
// 尝试获取锁
ReentrantLock lock = new ReentrantLock();
if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
    try {
        // 业务逻辑
    } finally {
        lock.unlock();
    }
}
```

> 在什么场景下必须使用ReentrantLock？

1. 需要公平锁时
2. 需要尝试获取锁（超时/中断响应）时
3. 需要多个条件变量时（synchronized只有一个waitSet）
4. 需要精确控制锁获取/释放时

---

### 4. synchronized的锁粗化与锁消除

什么是锁粗化？什么是锁消除？JVM如何实现？

**原理分析**

**锁粗化（Lock Coarsening）：**

将多个连续的加锁操作合并为一次加锁，减少频繁获取/释放锁的开销。

```java
// 优化前
synchronized (sb) { sb.append("a"); }
synchronized (sb) { sb.append("b"); }
synchronized (sb) { sb.append("c"); }

// 优化后（锁粗化）
synchronized (sb) {
    sb.append("a");
    sb.append("b");
    sb.append("c");
}
```

**锁消除（Lock Elision）：**

通过逃逸分析判断对象不会逃逸出线程，直接消除同步操作。

```java
// 线程安全：sb不会逃逸
public String builder(String s1, String s2, String s3) {
    StringBuffer sb = new StringBuffer();
    sb.append(s1);
    sb.append(s2);
    sb.append(s3);
    return sb.toString();
}
// JIT编译时可能消除synchronized
```

**实现位置：**

锁粗化和锁消除在JIT编译器的**c2编译器**阶段实现。

> 锁粗化有什么负面效果？

过度锁粗化可能导致：
- 本应并行的操作被串行化
- 持有锁的时间变长

但JVM会根据实际情况智能判断，通常利大于弊。

---

### 5. Monitor机制与ObjectMonitor的结构

synchronized的Monitor机制是怎样的？ObjectMonitor的结构是什么？

**原理分析**

**ObjectMonitor结构（C++）：**

```cpp
ObjectMonitor() {
    _header = NULL;        // 对象头
    _count = 0;           // 竞争计数
    _waiters = 0;        // 等待者数量
    _owner = NULL;       // 持有锁的线程
    _WaitSet = NULL;     // 等待队列（Object.wait）
    _EntryList = NULL;   // 阻塞队列（竞争锁失败）
}
```

**线程状态流转：**

```
竞争锁 → _EntryList（阻塞）
    ↓
获取锁成功 → _owner
    ↓
调用wait() → _WaitSet（等待）
    ↓
被notify()唤醒 → _EntryList
    ↓
再次竞争锁 → _owner
```

> 为什么重量级锁效率低？

1. **线程阻塞/唤醒**：需要操作系统介入，从用户态切换到内核态
2. **上下文切换**：每次阻塞/唤醒都需要保存/恢复线程上下文
3. **调度开销**：内核调度器需要参与

---

### 6. synchronized的可重入性原理

synchronized是如何实现可重入的？其原理是什么？

**原理分析**

**可重入性（Reentrant）：**

同一线程可以多次获取同一把锁，不会被自己阻塞。

```java
public synchronized void methodA() {
    methodB();  // 可重入
}

public synchronized void methodB() {
    // 仍然持有锁
}
```

**实现原理：**

在ObjectMonitor中记录持有锁的线程和重入次数：

```cpp
// _owner：持有锁的线程
// _count：重入次数（每次加锁+1，释放-1）

void ObjectMonitor::enter(TRAPS) {
    Thread* self = THREAD;
    if (self == _owner) {
        _count++;  // 重入次数+1
        return;
    }
    // 首次获取，执行CAS或自旋
}
```

> ReentrantLock的可重入与synchronized有何区别？

实现上都是通过计数器，但ReentrantLock更灵活：
```java
lock.lock();
lock.lock();  // 可重入，计数变为2
lock.unlock(); // 计数变为1
lock.unlock(); // 计数变为0，锁释放
```

---

### 7. synchronized与异常处理

synchronized方法抛出异常时，锁会自动释放吗？

**原理分析**

**自动释放机制：**

```java
public synchronized void method() {
    // 正常执行 → 锁释放
    // 抛出RuntimeException → 锁释放
    // 抛出Checked Exception → 锁释放
}
```

**monitorexit执行时机：**

```
synchronized块代码正常执行完成 → monitorexit
synchronized块代码抛出异常 → bytecode层面自动生成monitorexit
```

**字节码验证：**

```java
// 字节码（部分）
3: monitorenter          // 进入
13: athrow              // 抛出异常
14: aload_1
15: monitorexit        // 自动生成的释放
```

> 如果需要在finally中手动释放锁呢？

ReentrantLock需要手动释放（否则可能导致死锁）：

```java
ReentrantLock lock = new ReentrantLock();
try {
    lock.lock();
} finally {
    lock.unlock();  // 必须手动释放
}
```

---

### 8. synchronized对性能的影响及优化

synchronized对性能有什么影响？有哪些优化手段？

**原理分析**

**性能影响：**

1. **原子性保证**：所有操作串行化
2. **阻塞开销**：线程切换、上下文切换
3. **内存可见性**：缓存刷新、内存屏障

**优化手段：**

1. **减少锁持有时间**

```java
// 优化前
public synchronized void process() {
    doDatabaseOperation();
    doNetworkCall();
    updateState();
}

// 优化后
public void process() {
    synchronized (this) {
        updateState();
    }
    doDatabaseOperation();
    doNetworkCall();
}
```

2. **减小锁粒度**

```java
// 优化前：锁整个Map
Map<String, Object> map = new HashMap<>();
synchronized (map) { map.put(key, value); }

// 优化后：分段锁
ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
map.put(key, value);
```

3. **使用并发容器**

```java
ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();
CopyOnWriteArrayList<E> list = new CopyOnWriteArrayList<>();
BlockingQueue<E> queue = new LinkedBlockingQueue<>();
```

> 阿里Java开发手册关于synchronized的规定？

```java
// 正确方式
public class Test {
    private final Object lock = new Object(); // 私有锁
    public void test() {
        synchronized (lock) { // 同一实例
        }
    }
}
```

---

### 9. synchronized与volatile的对比

synchronized和volatile有什么区别？在什么场景下选择哪个？

**原理分析**

**对比：**

| 特性 | synchronized | volatile |
|-----|-------------|----------|
| 原子性 | ✓ | ✗ |
| 可见性 | ✓ | ✓ |
| 有序性 | ✓ | ✓ |
| 阻塞 | ✓ | ✗ |
| 性能 | 较低 | 较高 |

**选择原则：**

1. **volatile使用场景：**
   - 状态标志（boolean flag）
   - 单次读写（引用赋值、long/double）
   - 不需要原子复合操作

2. **synchronized使用场景：**
   - 需要保证原子性
   - 复合操作（先检查后执行）
   - 多个操作需要一起保证原子性

> 如何理解"volatile写 happens-before volatile读"？

这意味着：
- **volatile写之前的所有操作**不会被重排序到volatile写之后
- **volatile读之后的所有操作**不会被重排序到volatile读之前

---

### 10. synchronized的底层汇编指令

synchronized对应的汇编指令是什么？锁如何实现？

**原理分析**

**lock指令：**

synchronized的底层使用**lock**前缀指令：

```asm
; 实际汇编（x86）
lock cmpxchg %r15, (%rsi) ; lock cmpxchg指令
```

**lock指令的作用：**

1. **总线锁定**：确保原子性
2. **缓存失效**：实现可见性
3. **内存屏障**：实现有序性

**实现机制：**

1. **原子性**：通过CPU的lock前缀保证读-修改-写原子性
2. **可见性**：通过缓存一致性协议（MESI）实现
3. **有序性**：通过内存屏障实现

> 为什么早期 synchronized 性能差？

早期synchronized直接使用**重量级锁**：
- 每个synchronized都需要Monitor
- 线程竞争失败直接阻塞（内核态）
- 每次加锁/解锁都需要系统调用

JDK6引入锁升级后，性能大幅提升：
- 无竞争时使用偏向锁（无额外开销）
- 轻度竞争使用自旋（用户态）
- 只有重度竞争才使用重量级锁