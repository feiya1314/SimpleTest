# 1. NIO与IO模型

NIO与IO模型的详细内容已整合到 **15-IO模型.md**，请参考该文件。

主要包括以下知识点：
- BIO/NIO/AIO对比
- IO读写的内核原理
- 五种IO模型（同步阻塞、同步非阻塞、IO多路复用、信号驱动、异步IO）
- NIO三大组件（Buffer、Channel、Selector）
- select/poll/epoll对比
- TCP粘包/拆包
- 零拷贝（transferTo、mmap、DirectBuffer）
- NIO的OP_WRITE事件和read返回值处理
