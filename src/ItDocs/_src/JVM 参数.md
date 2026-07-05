---
title: JVM 参数
updated: 2026-05-02T00:34:36
created: 2019-06-05T23:24:28
---

1、Xms 、Xmx 是设置Java可以占用的最大内存吗？占用内存是否可以超过这个设置
**-Xms**：堆**初始内存**（起步多少） **-Xmx**：堆**最大内存**（上限多少） 生产建议 **两者设相同**，避免动态扩容，稳定 GC、防 OOM。

- jps 命令查看当前有哪些java线程
jps -l

- 使用 ps -mp pid -o THREAD,tid,time命令查看该进程的线程情况，查看哪个线程占用cpu多，tid代码线程ID，time代表这个线程的已运行时间。进制转换，2HEX，再将这3个TID转为16进制，为等会在jstack中查找方便
printf “%x\n” number

- 使用jstack查看进程信息，有了线程ID的16进制后，再在jstack中查看进程堆栈信息(之所有拿到TID信息，主要是为了查找方便)。通过jstack -pid再grep查询

**==jmap -heap pid 可以查看 JVM 各划分内存信息==**

using thread-local object allocation.
Parallel GC with 4 thread(s)

Heap Configuration: // 堆内存初始化配置
MinHeapFreeRatio = 0 // 堆最小空闲比率
MaxHeapFreeRatio = 100 // 堆最大空闲比率
MaxHeapSize = 734003200 (700.0MB) // 堆最大大小
NewSize = 89128960 (85.0MB) // 堆新生代大小
MaxNewSize = 244318208 (233.0MB) // 堆新生代最大大小
OldSize = 179306496 (171.0MB) //老年代大小
NewRatio = 2 //新生代和老年代的大小比率
SurvivorRatio = 8 // 年轻代种Eden区域survivor区的大小比率
MetaspaceSize = 21807104 (20.796875MB) //分配给类元数据空间的初始大小
CompressedClassSpaceSize = 1073741824 (1024.0MB) //类指针压缩空间大小
MaxMetaspaceSize = 17592186044415 MB //分配给类元数据空间的最大值, 超过此值就会触发Full GC. 此值仅受限于系统内存的大小, JVM会动态地改变此值
G1HeapRegionSize = 0 (0.0MB) //G1区块的大小, 取值为1M至32M. 其取值是要根据最小Heap大小划分出2048个区块

Heap Usage:
PS Young Generation // 新生代内存使用情况
Eden Space:
capacity = 67108864 (64.0MB)
used = 24848496 (23.697372436523438MB)
free = 42260368 (40.30262756347656MB)
37.02714443206787% used
From Space:
capacity = 11010048 (10.5MB)
used = 5863320 (5.591697692871094MB)
free = 5146728 (4.908302307128906MB)
53.254263741629465% used
To Space:
capacity = 11010048 (10.5MB)
used = 0 (0.0MB)
free = 11010048 (10.5MB)
0.0% used
PS Old Generation // 老年代内存使用情况
capacity = 179306496 (171.0MB)
used = 147488 (0.140655517578125MB)
free = 179159008 (170.85934448242188MB)
0.08225468864217836% used
---
可以使用 jmap -heap 10765 查看==新生代，老生代堆内存的分配大小以及使用情况==，看是否本身分配过小。
jmap -histo:live 10765 \| more 找到最耗内存的对象

可以 查看gc time等信息==，jstat -gcutil 26178 1000 100==

jstat -<span style='color:#006666'>\<</span><span style='color:#4F4F4F'>option</span><span style='color:#006666'>\></span> \[-t\] \[-h<span style='color:#006666'>\<</span><span style='color:#4F4F4F'>lines</span><span style='color:#006666'>\></span>\] <span style='color:#006666'>\<</span><span style='color:#4F4F4F'>vmid</span><span style='color:#006666'>\></span> \[<span style='color:#006666'>\<</span><span style='color:#4F4F4F'>interval</span><span style='color:#006666'>\></span> \[<span style='color:#006666'>\<</span><span style='color:#4F4F4F'>count</span><span style='color:#006666'>\></span>\]\]

<span style='font-size:11.0pt'>jstat </span><span style='font-size:10.5pt;color:#50A14F'>-gc -h3 </span><span style='font-size:11.0pt;color:#006666'>31736 1000 10 </span>
<span style='background: white'>表示分析进程id为31736 的gc情况，每隔1000ms打印一次记录，打印10次停止，每3行后打印指标头部</span>
**jstat -gcutil** 查看gc的统计信息

![image1](../../resources/43811e648a4240f6a86e978886b22a0f.png)
