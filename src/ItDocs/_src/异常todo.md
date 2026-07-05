---
title: 异常todo
updated: 2022-03-07T17:59:29
created: 2019-06-23T13:01:32
---

Java 编译后，会在代码后附加==异常表==的形式来实现 Java 的异常处理及 finally 机制

属性表（attribute_info）可以存在于 Class 文件、字段表、方法表中，用于描述某些场景的专有信息。属性表中有个 Code 属性，该属性在方法表中使用，Java 程序方法体中的代码被编译成的字节码指令存储在 Code 属性中。而异常表（exception_table）则是存储在 Code 属性表中的一个结构，这个结构是可选的。

异常表存储在每个方法的PermGen或Metaspace非堆空间中，如果方法中定义了try-catch block或finally block，将会创建异常表。
<span style='color:#333333'>异常表有四个字段：</span>
- <span style='color:#333333'>From : 开始点</span>
- <span style='color:#333333'>To：结束点</span>
- <span style='color:#333333'>Target：异常处理代码</span>
- <span style='color:#333333'>Type：异常类</span>
<span style='color:#333333'></span>
当抛出异常时，JVM会使用==异常表来定位异常处理者==。如果不存在异常处理逻辑，==栈帧生命周期结束==，同时异常将根据stack trace 被继续抛给上层调用方法。

每个类编译后，都会跟随一个异常表，如果发生异常，首先在异常表中查找对应的行（即代码中相应的try{}catch(){}代码块），如果找到，则跳转到异常处理代码执行，如果没有找到，则返回（执行 finally 之后），并 copy 异常的应用给父调用者，接着查询父调用的异常表，以此类推。

<span style='background:white'>如果程序触发了异常，Java 虚拟机会按照序号遍历异常表，当触发的异常在这条异常处理器的监控范围内（from 和 to），且异常类型（type）与该异常处理器一致时，Java 虚拟机就会跳转到该异常处理器的起始位置（target）开始执行字节码。</span>
<span style='background:white'>如果程序没有触发异常，那么虚拟机会使用 goto 指令跳过 catch 代码块，执行 finally 语句或者方法返回。</span>

<span style='font-style:italic;color:#979786'>// 源代码</span>  
<span style='font-weight:bold;color:#333333'>public static void </span><span style='font-weight:bold;color:#990000'>main</span>(<span style='font-weight: bold;color:#333333'>String</span>\[\] args) {  
<span style='font-weight:bold;color:#333333'>try</span> {  
<span style='font-style:italic;color:#979786'>// dosomething</span>  
System.out.<span style='color:#0086B3'>println</span>(<span style='color:#DD1144'>"enter try block"</span>);  
} <span style='font-weight:bold;color:#333333'>catch</span> (Exception e) {  
System.out.<span style='color:#0086B3'>println</span>(<span style='color:#DD1144'>"enter catch block"</span>);  
} finally {  
System.out.<span style='color:#0086B3'>println</span>(<span style='color:#DD1144'>"enter finally block"</span>);  
}  
}

<span style='font-style:italic;color:#979786'>// 字节码</span>  
<span style='color:teal'>0</span> getstatic \#<span style='color:teal'>2</span> \<java<span style='color:#009926'>/lang/</span>System.out\>  
<span style='color:teal'>3</span> ldc \#<span style='color:teal'>3</span> \<enter <span style='font-weight:bold;color:#333333'>try</span> block\>  
<span style='color:teal'>5</span> invokevirtual \#<span style='color:teal'>4</span> \<java<span style='color:#009926'>/io/</span>PrintStream.<span style='font-weight:bold; color:#333333'>println</span>\>  
<span style='color:teal'>8</span> getstatic \#<span style='color:teal'>2</span> \<java<span style='color:#009926'>/lang/</span>System.out\>  
<span style='color:teal'>11</span> ldc \#<span style='color:teal'>5</span> \<enter <span style='font-weight:bold;color:#333333'>finally</span> block\>  
<span style='color:teal'>13</span> invokevirtual \#<span style='color:teal'>4</span> \<java<span style='color:#009926'>/io/</span>PrintStream.<span style='font-weight:bold; color:#333333'>println</span>\>  
<span style='color:teal'>16</span> goto <span style='color:teal'>50</span> (+<span style='color:teal'>34</span>)  
<span style='color:teal'>19</span> astore_1  
<span style='color:teal'>20</span> getstatic \#<span style='color:teal'>2</span> \<java<span style='color:#009926'>/lang/</span>System.out\>  
<span style='color:teal'>23</span> ldc \#<span style='color:teal'>7</span> \<enter <span style='font-weight:bold;color:#333333'>catch</span> block\>  
<span style='color:teal'>25</span> invokevirtual \#<span style='color:teal'>4</span> \<java<span style='color:#009926'>/io/</span>PrintStream.<span style='font-weight:bold; color:#333333'>println</span>\>  
<span style='color:teal'>28</span> getstatic \#<span style='color:teal'>2</span> \<java<span style='color:#009926'>/lang/</span>System.out\>  
<span style='color:teal'>31</span> ldc \#<span style='color:teal'>5</span> \<enter <span style='font-weight:bold;color:#333333'>finally</span> block\>  
<span style='color:teal'>33</span> invokevirtual \#<span style='color:teal'>4</span> \<java<span style='color:#009926'>/io/</span>PrintStream.<span style='font-weight:bold; color:#333333'>println</span>\>  
<span style='color:teal'>36</span> goto <span style='color:teal'>50</span> (+<span style='color:teal'>14</span>)  
<span style='color:teal'>39</span> astore_2  
<span style='color:teal'>40</span> getstatic \#<span style='color:teal'>2</span> \<java<span style='color:#009926'>/lang/</span>System.out\>  
<span style='color:teal'>43</span> ldc \#<span style='color:teal'>5</span> \<enter <span style='font-weight:bold;color:#333333'>finally</span> block\>  
<span style='color:teal'>45</span> invokevirtual \#<span style='color:teal'>4</span> \<java<span style='color:#009926'>/io/</span>PrintStream.<span style='font-weight:bold; color:#333333'>println</span>\>  
<span style='color:teal'>48</span> aload_2  
<span style='color:teal'>49</span> athrow  
<span style='color:teal'>50 </span><span style='font-weight:bold;color:#333333'>return</span>

在字节码指令中，有三块重复的字节码指令，分别是8~13行、28~33行和40~45行，这三块重复的字节码就是 finally 代码块对应的代码。

三块重复字节码指令的原因是在 JVM 中，所有异常路径（如try、catch）以及所有正常执行路径的出口都会被附加一份 finally 代码块。也就是说，在上述的示例代码中，try 代码块后面会跟着一份 finally 的代码，catch 代码块后面也是如此，再加上原本正常流程会执行的 finally 代码块，在字节码中一共有三份 finally 代码块代码块。

针对每一条可能出现的异常的路径，JVM 都会在异常表中多生成一条异常处理器，用来监控整个 try-catch 代码块，同时它会捕获所有种类的异常，并且在执行完 finally 代码块之后会重新抛出刚刚捕获的异常。

Exception table:  
from to target type  
<span style='color:teal'> 0 </span> <span style='color:teal'> 8 </span> <span style='color:teal'> 19 </span> Class java/lang/Exception  
<span style='color:teal'> 0 </span> <span style='color:teal'> 8 </span> <span style='color:teal'> 39 </span> any  
<span style='color:teal'> 19 </span> <span style='color:teal'> 28 </span> <span style='color:teal'> 39 </span> any

<span style='background:white'>可以看到与原来相比异常表增加了两条，第2条异常处理器异常监控 try 代码块，第3条异常处理器监控 catch 代码块，如果出现异常则会跳转到第39行的 finally 代码块执行。</span>
<span style='background:white'>这就是 finally 一定会在 try-catch 代码块之后执行的原因了（某些能中断程序运行的操作除外）。</span>

<span style='background:white'>如果 finally 也抛出异常</span>
上文说到虚拟机会对整个 try-catch 代码块生成一个或多个异常处理器，如果在 catch 代码块中抛出了异常，这个异常会被捕获，并且在执行完 finally 代码块之后被重新抛出。

如果假设在 catch 代码块中抛出了异常 A，当执行 finally 代码块时又抛出了异常 B，那么最后抛出的是什么异常呢？
最后抛出的异常 B。也就是说，在捕获了 catch 代码块中的异常后，如果 finally 代码块中也抛出了异常，那么最终将会抛出 finally 中抛出的异常，而原来 catch 代码块中的异常将会被忽略。

<span style='background:white'>如果代码块中有 return</span>
如果 try 或者 catch 中有 return，finally 还会执行吗？如果 finally 中也有 return，那么最终返回的值是什么？

<span style='font-weight:bold;color:#333333'>public</span> static int <span style='font-weight:bold;color:#333333'>get</span>() {  
<span style='font-weight:bold;color:#333333'>try</span> {  
<span style='font-weight:bold;color:#333333'>return </span><span style='color:teal'>1</span>;  
} <span style='font-weight:bold;color:#333333'>catch</span> (Exception e) {  
<span style='font-weight:bold;color:#333333'>return </span><span style='color:teal'>2</span>;  
} <span style='font-weight:bold;color:#333333'>finally</span> {  
<span style='font-weight:bold;color:#333333'>return </span><span style='color:teal'>3</span>;  
}  
}
<span style='font-style:italic;color:#979786'>// 字节码指令</span>  
<span style='color:teal'>0</span> iconst_1  
<span style='color:teal'>1</span> istore_0  
<span style='color:teal'>2</span> iconst_3  
<span style='color:teal'>3</span> ireturn  
<span style='color:teal'>4</span> astore_0  
<span style='color:teal'>5</span> iconst_2  
<span style='color:teal'>6</span> istore_1  
<span style='color:teal'>7</span> iconst_3  
<span style='color:teal'>8</span> ireturn  
<span style='color:teal'>9</span> astore_2  
<span style='color:teal'>10</span> iconst_3  
<span style='color:teal'>11</span> ireturn

<span style='background:white'>finally 代码块会在所有正常及异常的路径上都复制一份，在这段字节码中，iconst_3 就是对应着 finally 代码块，共三份，所以即便在 try 或者 catch 代码块中有 return 语句，最终还是会会执行 finally 代码块中的内容。</span>
<span style='background:white'>也就是说，这个方法最终的返回结果是3。</span>

---
<https://www.ibm.com/developerworks/cn/java/j-jtp0924/index.html?ca=drs->

<https://chenyongjun.vip/articles/29>
异常底层原理

<https://lrwinx.github.io/2016/04/28/%E5%A6%82%E4%BD%95%E4%BC%98%E9%9B%85%E7%9A%84%E8%AE%BE%E8%AE%A1java%E5%BC%82%E5%B8%B8/>
如何优雅的设计java异常
<https://www.zhihu.com/question/30428214>
<https://www.zhihu.com/question/28254987/answer/40173231>
<https://blog.51cto.com/14230003/2379461>

<https://cloud.tencent.com/developer/article/1379381>
Exception底层原理是什么

<https://blog.liexing.me/2017/09/17/java-exception-table/>
Java 异常表与异常处理原理
<https://segmentfault.com/a/1190000022724917>
<https://droidyue.com/blog/2018/10/21/how-jvm-handle-exceptions/>

<https://zhuanlan.zhihu.com/p/55835404>
错误模型

深入了解计算机系统：
操作系统：异常的类别
中断、陷阱、故障、终止
![image1](../../resources/9c61ef4b2eb0452789409d83f64e85bd.png)

