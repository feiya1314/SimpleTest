---
title: Fork&Join框架
updated: 2026-05-07T00:27:07
created: 2019-09-01T17:35:13
---

原理
ForkJoinPool的核心在于其轻量级的调度机制，采用了Cilk的**work-stealing的基本调度策略：**

1、每个工作线程维持一个任务队列
2、任务队列以双端队列的形式维护，不仅支持先进后出的push和pop操作，还支持先进先出的take操作
3、由父任务fork出来的子任务被push到运行该父任务的工作线程对应的任务队列中
4、工作线程以先进后出的方式处理pop自己任务队列中的任务（优先处理最年轻的任务）
5、当任务队列中没有任务时，工作线程尝试随机从其他任务队列中窃取任务
6、当工作线程没有任务可以执行，且窃取不到任务时，它会“退出”（yiled、sleep、优先级调整），经过一段时间后再次尝试。除非其他所有的线程也都没有任务可以执行，这种情况下它们会一直阻塞直到有新的任务从上层添加进来

Fork/Join 是 JDK7 引入的**并行计算框架**，思想就四个字：**分治 + 并行**。
<span style='color:black'>核心逻辑：</span>
1.  <span style='color:black'>**Fork（拆分）**：把一个**大任务**递归拆成很多**小任务**，小任务小到阈值就不再拆；</span>
2.  <span style='color:black'>**Join（合并）**：等所有子任务执行完，把结果**汇总合并**；</span>
<span style='color:black'>适用场景：**大规模递归、海量数据遍历、排序、分块计算**（归并排序、大数据分片统计）。</span>
<span style='color:black'>它本质也是**线程池**，只是任务模型和普通线程池完全不一样。</span>
<span style='color:black'></span>
<span style='color:black'>二、底层核心特点</span>
1.  <span style='color:black'>基于**分治思想**：大拆小、小执行、结果合并</span>
2.  <span style='color:black'>自带**工作窃取（Work-Stealing）**</span>
- <span style='color:black'>每个线程有自己**双端队列**</span>
- <span style='color:black'>自己队列忙完，偷偷去**别的线程队列偷任务**执行</span>
- <span style='color:black'>极大减少线程空闲，CPU 利用率更高</span>
3.  <span style='color:black'>任务是 **ForkJoinTask**（有返回 / 无返回）</span>
4.  <span style='color:black'>==适合**计算密集型**，不适合阻塞 IO ，大数据量递归计算、数组排序、分片统计、海量数据遍历 → 用 Fork/Join==</span>

<span style='color:black'>三、普通线程池 ThreadPoolExecutor</span>
<span style='color:black'>**普通线程池特点：**</span>
1.  <span style='color:black'>**任务队列是共享队列**，所有线程抢同一个队列任务</span>
2.  <span style='color:black'>任务之间**没有父子拆分关系**，都是独立任务</span>
3.  <span style='color:black'>没有工作窃取，任务分配不灵活</span>
4.  <span style='color:black'>适合**普通业务任务、IO 密集型、独立任务**</span>
5.  <span style='color:black'>任务提交进去，排队依次执行，**不能递归拆分任务**</span>
<span style='color:black'></span>

---
<https://ifeve.com/forkjoinpool-%E6%8E%A2%E7%B4%A2/>

<https://www.cnblogs.com/senlinyang/p/7885964.html>

<https://www.cnblogs.com/lixuwu/p/7979480.html>

<https://www.jianshu.com/p/de025df55363>

<https://www.jb51.net/article/124612.htm>

原理
ForkJoinPool的核心在于其轻量级的调度机制，采用了Cilk的work-stealing的基本调度策略：

每个工作线程维持一个任务队列
任务队列以双端队列的形式维护，不仅支持先进后出的push和pop操作，还支持先进先出的take操作
由父任务fork出来的子任务被push到运行该父任务的工作线程对应的任务队列中
工作线程以先进后出的方式处理pop自己任务队列中的任务（优先处理最年轻的任务）
当任务队列中没有任务时，工作线程尝试随机从其他任务队列中窃取任务
当工作线程没有任务可以执行，且窃取不到任务时，它会“退出”（yiled、sleep、优先级调整），经过一段时间后再次尝试。除非其他所有的线程也都没有任务可以执行，这种情况下它们会一直阻塞直到有新的任务从上层添加进来

