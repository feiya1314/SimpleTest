
# 1. CAS与Atomic

CAS与Atomic的详细内容已整合到 **06并发包.md**，请参考该文件。

主要包括以下知识点：
- CAS底层原理（cmpxchg指令、LOCK前缀、缓存锁定）
- CAS三大问题（ABA、自旋过长、单变量限制）
- LongAdder分段CAS设计
- Unsafe类的核心功能
