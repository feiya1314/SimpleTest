
# 1. -Xms 和 -Xmx 的作用是什么？生产环境有什么建议？

**-Xms**：堆**初始内存**（起步多少）。**-Xmx**：堆**最大内存**（上限多少）。生产建议**两者设相同**，避免动态扩容，稳定 GC、防 OOM。

# 2. 如何排查 Java 进程内存占用过高的问题？

使用以下工具链排查：

- **jps -l**：查看当前所有 Java 进程，获取进程 PID
- **jmap -heap &lt;pid&gt;**：查看堆内存分配大小及使用情况，分析新生代、老年代是否分配过小
- **jmap -histo:live &lt;pid&gt; | more**：按对象占用内存排序，找到最耗内存的对象
- **jstat -gcutil &lt;pid&gt; &lt;interval&gt; &lt;count&gt;**：查看 GC 统计信息，判断 GC 频率是否异常

# 3. jmap -heap 输出中各区域参数的含义是什么？

输出解析：

- **MaxHeapSize**：堆最大大小
- **NewSize / MaxNewSize**：新生代初始/最大大小
- **OldSize**：老年代大小
- **NewRatio**：新生代与老年代大小比率
- **SurvivorRatio**：Eden 区与 Survivor 区的大小比率
- **MetaspaceSize / MaxMetaspaceSize**：元空间初始/最大值，超过 MaxMetaspaceSize 触发 Full GC
- **G1HeapRegionSize**：G1 区块大小，取值 1M~32M，根据最小 Heap 划分出 2048 个区块
- **MinHeapFreeRatio / MaxHeapFreeRatio**：堆最小/最大空闲比率

各区域 Usage 显示 capacity、used、free 及使用率，可判断内存水位是否正常。

![jstat-gcutil示例](../assets/01Java/43811e648a4240f6a86e978886b22a0f.png)

# 4. 如何排查 Java 进程 CPU 过高的问题？

1. **ps -mp &lt;pid&gt; -o THREAD,tid,time**：查看进程内各线程 CPU 占用情况，tid 为线程 ID，time 为已运行时间
2. **printf "%x\n" &lt;tid&gt;**：将高 CPU 的线程 ID 转为 16 进制
3. **jstack &lt;pid&gt; | grep &lt;十六进制tid&gt;**：在堆栈中定位对应线程，分析卡点

# 5. jstat 的常用用法有哪些？

- **jstat -gcutil &lt;pid&gt; &lt;interval&gt; &lt;count&gt;**：查看 GC 统计信息（各代使用率、GC 次数、GC 耗时）
- **jstat -gc -h3 &lt;pid&gt; 1000 10**：每 1000ms 打印一次 GC 详情，打印 10 次停止，每 3 行输出一次表头
