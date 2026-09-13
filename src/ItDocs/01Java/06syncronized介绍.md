1、synchronized方法和synchronized块
javac 在编译时，synchronized块会生成对应的monitorenter和monitorexit指令分别对应
synchronized同步块的进入和退出，有两个monitorexit指令的原因是：为了保证抛异常的情
况下也能释放锁，所以javac为同步代码块添加了一个隐式的try-finally，在finally中会调用
monitorexit命令释放锁。
synchronized方法  javac为其生成了一个ACC_SYNCHRONIZED关键字，在JVM进行方法
调用时，发现调用的方法被ACC_SYNCHRONIZED修饰，则会先尝试获得锁。
synchronzed内存语义进行了分析，当线程获取锁时会从主内存中获取共享变量的最新值，释放锁的时候会将共享变量同步到主内存中。从而，synchronized具有可见性。

```
在JVM底层，对于这两种synchronized语义的实现大致相同
```

![synchronized底层实现流程](../assets/01Java/22f1cf4e49a640408a4bed6eda463002.png)


2、锁的几种形式

传统的锁（也就是下文要说的重量级锁）依赖于系统的同步函数，在linux上使用mutex互斥锁，最底层实现依赖于futex，关于futex可以看我之前的文章，这些同步函数都涉及到用户态和内核态的切换、进程的上下文切换，成本较高。对于加了synchronized关键字但运行时并没有多线程竞争，或两个线程接近于交替执行的情况，使用传统锁机制无疑效率是会比较低的。传统锁一般使用cpu硬件提供的 CMPXCHG + lock实现

在JDK 1.6之前,synchronized只有传统的锁机制，因此给开发者留下了synchronized关键字相比于其他同步机制性能不好的印象。

在JDK 1.6引入了两种新型锁机制：偏向锁和轻量级锁，它们的引入是为了解决在没有多线程竞争或基本没有竞争的场景下因使用传统锁机制带来的性能开销问题。

在看这几种锁机制的实现前，我们先来了解下对象头，它是实现多种锁机制的基础。



3、对象头

```
Java对象保存在内存中时，由以下三部分组成：
1，对象头  2，实例数据  3，对齐填充字节

Java中任意对象都可以用作锁，因此必定要有一个映射关系，存储该对象以及其对应的锁信息（比如当前哪个线程持
```

有锁，哪些线程在等待），如果用全局map，会有一些问题：需要对map做线程安全保障，不同的synchronized之间
会相互影响，性能差；另外当同步对象较多时，该map可能会占用比较多的内存。

可以将这个映射关系存储在对象头中，因为对象头本身也有一些hashcode、GC相关的数据。在JVM中，对象在内存
中除了本身的数据外还会有个对象头，对于普通对象而言，其对象头中有两类信息：mark word和类型指针。另外对
于数组而言还会有一份记录数组长度的数据。

类型指针是指向该对象所属类对象的指针，mark word用于存储对象的HashCode、GC分代年龄、锁状态等信息。
在32位系统上mark word长度为32bit，64位系统上长度为64bit。为了能在有限的空间里存储下更多的数据，其存
储格式是不固定的，在32位系统上各状态的格式如下：

![Mark Word 32位结构](../assets/01Java/43d9b40e7e6f404aaba640ebde8ba8af.png)

锁信息也是存在于对象的mark word中的。当对象状态为偏向锁（biasable）时，mark word存储的是偏向的线程
ID；当状态为轻量级锁（lightweight locked）时，mark word存储的是指向线程栈中Lock Record的指针；当状态
为重量级锁（inflated）时，为指向堆中的monitor对象的指针,在Java虚拟机（HotSpot）中，Monitor是由
ObjectMonitor实现的,C++实现。

升级为偏向锁、轻量级或者重量级锁后，hashcode会存放到其他地方
偏向锁状态：
对象刚创建、没执行过 obj.hashCode() MarkWord 里压根不存 HashCode
一旦主动调用了 hashCode()，会直接废掉偏向锁：对象 直接进入 无锁 → 跳过偏向锁，直接走轻量级锁，不会再进入偏向锁状态，空间被占满，放不下 HashCode。只要对象生成了 HashCode，就直接禁用偏向锁
偏向锁 → 轻量级锁：
JVM 会在当前加锁线程的栈帧中，开辟一块 Lock Record（锁记录），把原对象 MarkWord 完整拷贝进去：
轻量级锁 → 重量级锁
旧的 MarkWord 信息依然保留在线程栈锁记录，
对象 MarkWord 改写为：指向 ObjectMonitor 监视器地址 + 重量级锁标记。

等锁释放、变回无锁状态时，再回填恢复到对象 Mark Word

Lock Record是线程私有的数据结构，每一个线程都有一个可用Lock Record列表，同时还有一个全局
的可用列表。每一个被锁住的对象Mark Word都会和一个Lock Record关联（对象头的MarkWord中的
Lock Word指向Lock Record的起始地址），同时Lock Record中有一个Owner字段存放拥有该锁的线
程的唯一标识（或者 object mark word ），表示该锁被这个线程占用。

Owner     初始时为NULL表示当前没有任何线程拥有该monitor record，当线程成功拥有该锁后保存线程唯一标识，当锁被释放时又设置为NULL；
EntryQ     关联一个系统互斥锁（semaphore），阻塞所有试图锁住monitor record失败的线程；
RcThis       表示blocked或waiting在该monitor record上的所有线程的个数；
Nest            用来实现 重入锁的计数；
HashCode        保存从对象头拷贝过来的HashCode值（可能还包含GC age）。
Candidate      用来避免不必要的阻塞或等待线程唤醒，因为每一次只有一个线程能够成功拥有
锁，如果每次前一个释放锁的线程唤醒所有正在阻塞或等待的线程，会引起不必要
的上下文切换（从阻塞到就绪然后因为竞争锁失败又被阻塞）从而导致性能严重下
降。Candidate只有两种可能的值0表示没有需要唤醒的线程1表示要唤醒一个继任

线程来竞争锁。


HotSpot对对象头的定义如下图：

![锁升级与Mark Word变化](../assets/01Java/5cec5b4d2208425e8389c632faf177d3.png)

与一切皆对象一样，所有的Java对象是天生的Monitor，每一个Java对象都有成为Monitor的潜质，因为在Java的设计中 ，每一个Java对象自打娘胎里出来就带了一把看不见的锁，它叫做内部锁或者Monitor锁。 	 	也就是通常说Synchronized的对象锁，MarkWord锁标识位为10，其中指针指向的是Monitor对象的起始地址。在Java虚拟机（HotSpot）中，Monitor是由ObjectMonitor实现的，其主要数据结构如下（位于HotSpot虚拟机源码ObjectMonitor.hpp文件，C++实现的）(monitor其实就是下面数据结构的实例，Java中每个对象都会有一个monitor)：  锁升级过程 	1、偏向锁是指当一段同步代码一直被同一个线程所访问时，即不存在多个线程的竞争时，那么该线程在后续访问时便会自动获得锁，从而降低获取锁带来的消耗，即提高性能。 	2、轻量级锁是指当锁是偏向锁的时候，却被另外的线程所访问，此时偏向锁就会升级为轻量级锁， 	3、如果轻量级锁的更新操作失败了，虚拟机首先会检查对象的 Mark Word 是否指向当前线程的栈帧，如果是就说明当前线程已经拥有了这个对象的锁，那就可以直接进入同步块继续执行，否则说明多个线程竞争锁。 		若当前只有一个等待线程，则该线程将通过自旋进行等待。但是当自旋超过一定的次数时，轻量级锁便会升级为重量级锁（锁膨胀）。 		另外，当一个线程已持有锁，另一个线程在自旋，而此时又有第三个线程来访时，轻量级锁也会升级为重量级锁（锁膨胀）。


4、重量级锁
重量级锁是我们常说的传统意义上的锁，其利用操作系统底层的同步机制去实现Java中的线程同步。

```
重量级锁的状态下，对象的mark word为指向一个堆中monitor对象的指针。一个monitor对象包括这么几个关键字段：cxq竞争队列（下图中的ContentionList），EntryList入口队列 ，WaitSet，owner。
其中cxq ，EntryList ，WaitSet都是由ObjectWaiter的链表结构，owner指向持有锁的线程。
```

![ObjectMonitor队列结构](../assets/01Java/6fa8afed4adf4ae8ba0d57ef5cc20464.png)	1、当一个线程尝试获得锁时，如果该锁已经被占用，则会将该线程封装成一个ObjectWaiter对象插入到cxq的队列尾部，然后暂停当前线程。
2、当持有锁的线程释放锁前，会将cxq中的所有元素移动到EntryList入口队列中去，并唤醒EntryList的队首线程。
3、尝试获取锁，可能会失败，原因是非公平队列，新线程会首先尝试CAS获取锁，可能恰好获取成功

如果一个线程在同步块中调用了Object#wait方法，会将该线程对应的ObjectWaiter从EntryList移除并加入到WaitSet中，然后释放锁。当wait的线程被notify之后，会将对应的ObjectWaiter从WaitSet移动到EntryList中。


5、轻量级锁
在Java程序运行时，同步块中的代码都是不存在竞争的，不同的线程交替的执行同步块中的代码。这种情况下，用重量级锁是没必要的。因此JVM引入了轻量级锁的概念。

线程在执行同步块之前，JVM会先在当前的线程的栈帧中创建一个Lock Record，其包括一个用于存储对象头中的 mark word（官方称之为Displaced Mark Word）以及一个指向对象的指针。下图右边的部分就是一个Lock Record。

![Lock Record结构](../assets/01Java/8545cca2943e43cbaaf178186684a6ea.png)加锁过程
1.在线程栈中创建一个Lock Record，将其obj（即上图的Object reference）字段指向锁对象。

2.直接通过CAS指令将Lock Record的地址存储在对象头的mark word中，如果对象处于无锁状态则修改成功，代表该线程获得了轻量级锁。如果失败，进入到步骤3。

3.如果是当前线程已经持有该锁了，代表这是一次锁重入。设置Lock Record第一部分（Displaced Mark Word）为null，起到了一个重入计数器的作用。然后结束。

4.走到这一步说明发生了竞争，需要膨胀为重量级锁。

解锁过程
1.遍历线程栈,找到所有obj字段等于当前锁对象的Lock Record。

2.如果Lock Record的Displaced Mark Word为null，代表这是一次重入，将obj设置为null后continue。

3.如果Lock Record的Displaced Mark Word不为null，则利用CAS指令将对象头的mark word恢复成为Displaced Mark Word。如果成功，则continue，否则膨胀为重量级锁。

6、偏向锁
有时为保证多线程运行正常会加入如synchronized这样的同步语义。但是在应用在实际运行时，很可能只有一个线程会调用相关同步方法。因此在JDK1.6中为了提高一个对象在一段很长的时间内都只被一个线程用做锁对象场景下的性能，引入了偏向锁，在第一次获得锁时，会有一个CAS操作，之后该线程再获取锁，只会执行几个简单的命令，而不是开销相对较大的CAS命令。

对象创建
当JVM启用了偏向锁模式（1.6以上默认开启），当新创建一个对象的时候，如果该对象所属的class没有关闭偏向锁模式（什么时候会关闭一个class的偏向模式下文会说，默认所有class的偏向模式都是是开启的），那新创建对象的mark word将是可偏向状态，此时mark word中的thread id（参见上文偏向状态下的mark word格式）为0，表示未偏向任何线程，也叫做匿名偏向(anonymously biased)。

加锁过程
case 1：当该对象第一次被线程获得锁的时候，发现是匿名偏向状态，则会用CAS指令，将mark word中的thread id由0改成当前线程Id。如果成功，则代表获得了偏向锁，继续执行同步块中的代码。否则，将偏向锁撤销，升级为轻量级锁。

case 2：当被偏向的线程再次进入同步块时，发现锁对象偏向的就是当前线程，在通过一些额外的检查后（细节见后面的文章），会往当前线程的栈中添加一条Displaced Mark Word为空的Lock Record中，然后继续执行同步块的代码，因为操纵的是线程私有的栈，因此不需要用到CAS指令；由此可见偏向锁模式下，当被偏向的线程再次尝试获得锁时，仅仅进行几个简单的操作就可以了，在这种情况下，synchronized关键字带来的性能开销基本可以忽略。

case 3.当其他线程进入同步块时，发现已经有偏向的线程了，则会进入到撤销偏向锁的逻辑里，一般来说，会在safepoint中去查看偏向的线程是否还存活，如果存活且还在同步块中则将锁升级为轻量级锁，原偏向的线程继续拥有锁，当前线程则走入到锁升级的逻辑里；如果偏向的线程已经不存活或者不在同步块中，则将对象头的mark word改为无锁状态（unlocked），之后再升级为轻量级锁。

由此可见，偏向锁升级的时机为：当锁已经发生偏向后，只要有另一个线程尝试获得偏向锁，则该偏向锁就会升级成轻量级锁。当然这个说法不绝对，因为还有批量重偏向这一机制。

解锁过程
当有其他线程尝试获得锁时，是根据遍历偏向线程的lock record来确定该线程是否还在执行同步块中的代码。因此偏向锁的解锁很简单，仅仅将栈中的最近一条lock record的obj字段设置为null。需要注意的是，偏向锁的解锁步骤中并不会修改对象头中的thread id。

![偏向锁解锁步骤](../assets/01Java/91f7e98f2d42497fadb82b7d6c5efd93.png)

另外，偏向锁默认不是立即就启动的，在程序启动后，通常有几秒的延迟，可以通过命令 -XX:BiasedLockingStartupDelay=0来关闭延迟。


Synchronized的实现,实现如下图所示；

![Synchronized实现概览](../assets/01Java/3e51bbe2703b44c2b110093c2f7c4686.png)

它有多个队列，当多个线程一起访问某个对象监视器的时候，对象监视器会将这些线程存储在不同的容器中。

Contention List：竞争队列，所有请求锁的线程首先被放在这个竞争队列中；

Entry List：Contention List中那些有资格成为候选资源的线程被移动到Entry List中；

Wait Set：那些调用wait方法被阻塞的线程被放置在这里；

OnDeck：任意时刻，最多只有一个线程正在竞争锁资源，该线程被成为OnDeck；

Owner：当前已经获取到所资源的线程被称为Owner；

!Owner：当前释放锁的线程。

JVM每次从队列的尾部取出一个数据用于锁竞争候选者（OnDeck），但是并发情况下，ContentionList会被大量的并发线程进行CAS访问，为了降低对尾部元素的竞争，JVM会将一部分线程移动到EntryList中作为候选竞争线程。Owner线程会在unlock时，将ContentionList中的部分线程迁移到EntryList中，并指定EntryList中的某个线程为OnDeck线程（一般是最先进去的那个线程）。Owner线程并不直接把锁传递给OnDeck线程，而是把锁竞争的权利交给OnDeck，OnDeck需要重新竞争锁。这样虽然牺牲了一些公平性，但是能极大的提升系统的吞吐量，在JVM中，也把这种选择行为称之为“竞争切换”。

OnDeck线程获取到锁资源后会变为Owner线程，而没有得到锁资源的仍然停留在EntryList中。如果Owner线程被wait方法阻塞，则转移到WaitSet队列中，直到某个时刻通过notify或者notifyAll唤醒，会重新进去EntryList中。

处于ContentionList、EntryList、WaitSet中的线程都处于阻塞状态，该阻塞是由操作系统来完成的（Linux内核下采用pthread_mutex_lock内核函数实现的）。


![Monitor对象结构](../assets/01Java/facd6a024483403dab52ecf22f91e2fc.png)

```
(monitor其实就是下面数据结构的实例，Java中每个对象都会有一个monitor)：
ObjectMonitor() {
    \_header       = NULL;
    \_count        = 0; // 记录个数
    \_waiters      = 0,
    \_recursions   = 0;
    \_object       = NULL;
    \_owner        = NULL;
    \_WaitSet      = NULL; // 处于wait状态的线程，会被加入到\_WaitSet
    \_WaitSetLock  = 0 ;
    \_Responsible  = NULL ;
    \_succ         = NULL ;
    \_cxq          = NULL ;
    FreeNext      = NULL ;
    \_EntryList    = NULL ; // 处于等待锁block状态的线程，会被加入到该列表
    \_SpinFreq     = 0 ;
    \_SpinClock    = 0 ;
    OwnerIsThread = 0 ;

每个锁对象都有两个队列，就绪队列以及阻塞队列。就绪队列存储了将要获得锁的线程，阻塞队列存储了被阻塞的线程。一个线程被唤醒后，才会进入就绪队列，以等待CPU调度。反之一个线程被wait后就会进入阻塞队列，等待下一次唤醒。 也就是说一个线程被wait后会进入阻塞队列，待调用了 notify或notifyAll之后，该线程就会进入就绪队列。

• 重量级锁是指当有一个线程获取锁之后，其余所有等待获取该锁的线程都会处于阻塞状态。
重量级锁通过对象内部的监视器（monitor）实现，而其中 monitor 的本质是依赖于底层操作系统的 Mutex Lock （互斥锁）实现，操作系统实现线程之间的切换需要从用户态切换到内核态，切换成本非常高。
简言之，就是所有的控制权都交给了操作系统，由操作系统来负责线程间的调度和线程的状态变更。而这样会出现频繁地对线程运行状态的切换，线程的挂起和唤醒，从而消耗大量的系统资源，导致性能低下。
```


偏向锁的释放：
偏向锁的撤销在上述第四步骤中有提到。偏向锁只有遇到其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁，线程不会主动去释放偏向锁。偏向锁的撤销，需要等待全局安全点（在这个时间点上没有字节码正在执行），它会首先暂停拥有偏向锁的线程，判断锁对象是否处于被锁定状态，撤销偏向锁后恢复到未锁定（标志位为“01”）或轻量级锁（标志位为“00”）的状态。

偏向锁的适用场景
始终只有一个线程在执行同步块，在它没有执行完释放锁之前，没有其它线程去执行同步块，在锁无竞争的情况下使用，一旦有了竞争就升级为轻量级锁，升级为轻量级锁的时候需要撤销偏向锁，撤销偏向锁的时候会导致stop the word操作；
在有锁的竞争时，偏向锁会多做很多额外操作，尤其是撤销偏向所的时候会导致进入安全点，安全点会导致stw，导致性能下降，这种情况下应当禁用；


首先简单说下先偏向锁、轻量级锁、重量级锁三者各自的应用场景：
• 偏向锁：只有一个线程进入临界区；
• 轻量级锁：多个线程交替进入临界区；
• 重量级锁：多个线程同时进入临界区。
还要明确的是，偏向锁、轻量级锁都是JVM引入的锁优化手段，目的是降低线程同步的开销。比如以下的同步代码块：
synchronized (lockObject) {
// do something
}
上述同步代码块中存在一个临界区，假设当前存在Thread#1和Thread#2这两个用户线程，分三种情况来讨论：
• 情况一：只有Thread#1会进入临界区；同步块中的代码都是不存在竞争的
• 情况二：Thread#1和Thread#2交替进入临界区；同步块中的代码都是不存在竞争的
• 情况三：Thread#1和Thread#2同时进入临界区。

上述的情况一是偏向锁的适用场景，此时当Thread#1进入临界区时，JVM会将lockObject的对象头Mark Word的锁标志位设为“01”，同时会用CAS操作把Thread#1的线程ID记录到Mark Word中，此时进入偏向模式。所谓“偏向”，指的是这个锁会偏向于Thread#1，若接下来没有其他线程进入临界区，则Thread#1再出入临界区无需再执行任何同步操作。也就是说，若只有Thread#1会进入临界区，实际上只有Thread#1初次进入临界区时需要执行CAS操作，以后再出入临界区都不会有同步操作带来的开销。

然而情况一是一个比较理想的情况，更多时候Thread#2也会尝试进入临界区。若Thread#2尝试进入时Thread#1已退出临界区，即此时lockObject处于未锁定状态，这时说明偏向锁上发生了竞争（对应情况二），此时会撤销偏向，Mark Word中不再存放偏向线程ID，而是存放hashCode和GC分代年龄，同时锁标识位变为“01”（表示未锁定），这时Thread#2会获取lockObject的轻量级锁。因为此时Thread#1和Thread#2交替进入临界区，所以偏向锁无法满足需求，需要膨胀到轻量级锁。

再说轻量级锁什么时候会膨胀到重量级锁。若一直是Thread#1和Thread#2交替进入临界区，那么没有问题，轻量锁hold住。一旦在轻量级锁上发生竞争，即出现“Thread#1和Thread#2同时进入临界区”的情况，轻量级锁就hold不住了。 （根本原因是轻量级锁没有足够的空间存储额外状态，此时若不膨胀为重量级锁，则所有等待轻量锁的线程只能自旋，可能会损失很多CPU时间）

膨胀的条件： 		1.偏向锁膨胀：线程A持有锁，其他线程请求过锁就会膨胀，也就是说对于锁来说，只要有超过一个线程请求过锁，注意是请求过，偏向锁就会膨胀成轻量级锁。 		 		2.轻量级锁膨胀：线程A在运行中持有锁，线程B竞争锁，线程B会首先自旋，自旋超时之后会膨胀成重量级锁，注意条件是发生竞争。 		 		3.重量级锁是最高的级别，不存在膨胀。 		 	锁降级： 		没有 JDK 标准，完全看各家 JVM 是咋实现的了。 		像 HotSpot JVM 其实就支持锁降级，但是锁升降级效率较低，如果频繁升降级的话对性能就会造成很大影响。重量级锁降级发生于 STW 阶段，降级对象为仅仅能被 VMThread 访问而没有其他 JavaThread 访问的对象。 		被锁的对象都被垃圾回收了有没有锁还有啥关系？因此基本认为锁不可降级。 		原文： 			In its current implementation, monitor deflation is performed during every STW pause, while all Java threads are waiting at a safepoint. We have seen safepoint cleanup stalls up to 200ms on monitor-heavy-applications.


自己实现一个基于CAS的自旋锁：

![偏向锁适用场景](../assets/01Java/02963f6fb0764bb0abbbe01023c428ed.png)

