---
title: socket
updated: 2023-12-09T18:51:00
created: 2022-10-23T15:57:39
---

当**并发操作很多，线程切换很频繁，cpu load很高的时候，就会出现SocketInputStream.socketRead0的问题**，这并非阻塞在ServerSocket.accept()而是**阻塞在获取流**。

![image1](../../resources/63b1bc6be0104266b76f993dfcba12b0.png)

进行远程通信时，在客户程序中，线程在以下情况可能进入阻塞状态：
1、请求与服务器建立连接时，即当线程执行Socket的带参数的构造方法，或执行Socket的connect()方法时，会进入阻塞状态，直到连接成功，此线程才从Socket的构造方法或connect()方法返回。
2、程从Socket的输入流==读入数据时，如果没有足够的数据，就会进入阻塞状态==，直到读==到了足够的数据==，或者到达输入流的末尾，或者出现了异常，才从输入流的read()方法返回或异常中断。
3、线程向Socket的输出流写一批数据时，可能会进入阻塞状态，等到输出了所有的数据，或者出现异常，才从输出流的write()方法返回或异常中断。
4、当调用Socket的setSoLinger()方法设置了关闭Socket的延迟时间，那么当线程执行Socket的close()方法时，会进入阻塞状态，直到底层Socket发送完所有剩余数据，或者超过了setSoLinger()方法设置的延迟时间，才从close()方法返回。

在服务器程序中，线程在以下情况可能会进入阻塞状态：
1、线程执行ServerSocket的accept()方法，等待客户的连接，直到接收到了客户连接，才从accept()方法返回。
2、线程从Socket的输入流读入数据时, 如果输入流没有足够的数据，就会进入阻塞状态。
3、线程向Socket的输出流写一批数据时，可能会进入阻塞状态，等到输出了所有的数据，或者出现异常，才从输出流的write()方法返回或异常中断。

---
<https://blog.csdn.net/u013613428/article/details/52171367>

<https://developer.aliyun.com/article/573041>

---
输入流中有多少数据才算足够呢？这要看线程执行的read()方法的类型：
1\. int read()：只要输入流中有一个字节，就算足够。
2\. int read(byte\[\] buff)：只要输入流中的字节数目与参数buff数组的长度相同就算足够。
3\. String readLine()：只要输入流中有一行字符串，就算足够。值得注意的是InputStream类并没有readLine()方法，在过滤流BufferedReader类中才有此方法。
