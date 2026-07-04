
# 1. String为什么是不可变的？设计成不可变有什么好处？

**不可变类的五条规则：**

1. 不提供任何修改对象状态的方法
2. 保证类不会被扩展（类声明为final）
3. 所有字段都是final的
4. 所有字段都是private的
5. 确保对可变组件的互斥访问

**String的实现：**

```java
public final class String {
    private final char[] value;  // final + private
    // substring、concat、replace等方法都重新new String，不修改原对象
}
```

**String的value是final和private的**，所有看似修改的方法（substring、concat、replace）实际上都返回一个新String对象。

**设计成不可变的好处：**

1. **安全性**：
   - 线程安全，不可变天生线程安全
   - 常作为HashMap的key，可变会导致key变化
   - 常作为数据库或接口的参数

2. **效率**：
   - 字符串常量池节省空间
   - hashcode可缓存，不用重复计算

**String真的完全不可变吗？**

通过反射可以修改String的值：
```java
String str = "123";
Field field = String.class.getDeclaredField("value");
field.setAccessible(true);
char[] value = (char[]) field.get(str);
value[1] = '3';  // str变为"133"
```

# 2. 字符串常量池的原理与位置变迁

字符串常量池在JDK不同版本中的位置有什么变化？

**原理分析**

**字符串常量池（String Pool）** 是JVM为了提升性能和节省内存而维护的一个特殊内存区域，类似一个系统级别的缓存。有两种方式将字符串放入常量池：
- 直接使用双引号声明的String对象直接存储在常量池中
- 使用String提供的intern方法

**JDK版本位置变迁：**

| JDK版本 | 字符串常量池位置 | 方法区实现 |
|---------|----------------|-----------|
| JDK 6及以前 | **Perm（永久代）** | 永久代（堆内） |
| JDK 7 | **堆（Heap）** | 永久代（堆内） |
| JDK 8 | **堆（Heap）** | **元空间**（堆外本地内存） |

**为何移动（JDK7）：**

永久代默认大小只有4m，字符串常量池容易引发OOM。移到堆后可以使用更大的内存空间。

**关键区分：**

- **运行时常量池**：一直在方法区，存放类版本、字段、方法、接口等描述信息
- **字符串常量池**：JDK7前在方法区（Perm），JDK7后在堆中
- **Class常量池**：每个class文件都有一个常量池，存放字符串常量、类接口名字、字段名等

# 3. intern方法的原理

intern方法是如何工作的？有什么注意事项？

**工作机制：**

```java
String s1 = "hello";           // 字面量，直接存入常量池
String s2 = new String("hello"); // 堆中创建新对象
String s3 = s2.intern();        // 常量池已有"hello"，返回常量池引用

s1 == s2  // false（堆对象 vs 常量池）
s1 == s3  // true（都是常量池引用）
```

intern方法通过JNI调用C++实现的**StringTable**（类似固定大小的HashMap，默认大小1009）：

- 如果常量池中存在当前字符串，直接返回常量池中的引用
- 如果常量池中没有，将当前字符串放入常量池再返回

**性能注意：**

StringTable是固定大小的HashTable，不能自动扩容，默认大小1009。放入大量字符串会导致Hash冲突严重，链表变长，intern性能大幅下降。可通过 `-XX:StringTableSize=N` 调大。

# 4. String、StringBuilder、StringBuffer的区别

**对比分析：**

| 特性 | String | StringBuilder | StringBuffer |
|------|--------|--------------|-------------|
| **可变性** | 不可变 | 可变 | 可变 |
| **线程安全** | 安全（不可变） | 不安全 | 安全（synchronized） |
| **性能** | 最差（每次修改创建新对象） | 最高 | 中等 |
| **使用场景** | 字符串不常变或需线程安全 | 单线程字符串拼接 | 多线程字符串拼接 |

**底层实现：**

StringBuilder和StringBuffer都继承自**AbstractStringBuilder**，内部维护一个可变字符数组 `char[] value`。当容量不够时自动扩容（创建新数组并拷贝，**容量变为原来的2倍+2**）。

**选择建议：**

- 单线程下大量拼接优先使用**StringBuilder**
- 多线程下字符串拼接使用**StringBuffer**或StringBuilder加外部锁
- 少量拼接直接用"+"，编译器会优化为StringBuilder

# 5. 字符串拼接的底层优化

Java中字符串拼接的编译期和运行期分别做了什么优化？

**编译期优化（常量折叠）：**

```java
// 编译期确定，直接优化为一个字符串
String s = "a" + "b" + "c";  // 编译后: String s = "abc";
```

**运行期优化（使用StringBuilder）：**

```java
// JDK5之前使用StringBuffer，之后使用StringBuilder
String s = a + b + c;
// 编译后等价于:
// StringBuilder sb = new StringBuilder();
// sb.append(a).append(b).append(c);
// String s = sb.toString();
```

**循环拼接的陷阱：**

```java
// 错误写法：每次循环都new一个StringBuilder
String s = "";
for (int i = 0; i < 1000; i++) {
    s += i;  // 每次创建StringBuilder和String对象
}

// 正确写法：手动使用StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String s = sb.toString();
```

在循环中使用"+"拼接，编译器每次都会创建新的StringBuilder对象，性能极差。**循环内拼接必须手动创建StringBuilder**。
