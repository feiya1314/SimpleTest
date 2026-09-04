# 1、Spring Boot 的启动流程

Spring Boot 启动 = 准备环境 + 构建 Spring 容器 + 自动配置 + 启动 Tomcat

总览 10 步：

1. 从 `META-INF/spring.factories` 加载 `ApplicationContextInitializer`、`ApplicationListener`
2. 推断 Web 应用类型（Servlet / Reactive / 普通）
3. 加载 `SpringApplicationRunListeners`，发布 `ApplicationStartingEvent`
4. 构建 `Environment`，加载所有配置源
5. 发布 `ApplicationEnvironmentPreparedEvent`
6. 创建 `ApplicationContext`
7. **核心：`refreshContext()` 刷新容器** —— 扫描 Bean、执行 `BeanFactoryPostProcessor`、注册 `BeanPostProcessor`、实例化单例 Bean
8. 启动内嵌 Web 服务器（Tomcat / Jetty / Undertow），注册 `DispatcherServlet`
9. 发布启动完成事件
10. 应用启动完成，对外提供服务

## 阶段一：启动前置准备（SpringApplication 初始化）

### 1. 从 SPI 加载扩展组件

依靠 `SpringFactoriesLoader` 完成，是 Spring Boot 可扩展机制的核心：

- 扫描类路径下所有 `META-INF/spring.factories`（含第三方 jar），以 Key-Value 形式读取扩展配置
- 核心加载两类组件：
  - `ApplicationContextInitializer`：容器初始化器，上下文创建后、刷新前对 ApplicationContext 做自定义设置（注册属性源、添加 BeanFactory 后置处理器等）
  - `ApplicationListener`：应用监听器，监听启动各阶段生命周期事件
- Spring Boot 2.7+ 自动配置类新增 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件方式（3.x 推荐），但初始化器和监听器仍保留在 `spring.factories` 中

### 2. 推断 Web 应用类型

根据类路径是否存在相关实现类，自动判断运行模式：

- **Servlet 类型**：存在 `DispatcherServlet` 与 Servlet API → Servlet Web 上下文 + 内嵌 Tomcat/Jetty/Undertow
- **Reactive 类型**：存在 `DispatcherHandler` → 响应式 Web 上下文 + Netty
- **普通类型**：两者都不存在 → 标准 Java 应用，无内嵌 Web 容器

### 3. 加载运行监听器并发布启动事件

- 通过 `SpringFactoriesLoader` 加载 `SpringApplicationRunListener` 实现类，核心实现是 `EventPublishingRunListener`
- 作用：**在启动各节点发布生命周期事件**，将启动流程暴露给扩展点
- 首个事件 `ApplicationStartingEvent`：标识应用开始启动，此时上下文、环境均未创建

## 阶段二：环境准备（Environment 构建）

### 4. 加载配置环境，发布环境就绪事件

1. 根据 Web 类型创建对应环境对象：
   - Servlet：`StandardServletEnvironment`
   - Reactive：`StandardReactiveWebEnvironment`
   - 普通：`StandardEnvironment`
2. 按优先级加载所有配置源（PropertySource）：命令行参数 > 系统环境变量 > application.properties/yml > profile 环境配置
3. 环境构建完成后发布 `ApplicationEnvironmentPreparedEvent`
   - Nacos、Apollo 等配置中心通过监听此事件，将远程配置注入 Environment

## 阶段三：创建应用上下文（ApplicationContext）

### 5. 实例化 Spring 容器

根据 Web 类型创建对应 ApplicationContext 实现：

| 应用类型 | 上下文实现类 | 内置核心能力 |
| --- | --- | --- |
| Servlet Web | `AnnotationConfigServletWebServerApplicationContext` | 注解读取器 + 类路径扫描器 + Web 容器 |
| Reactive Web | `AnnotationConfigReactiveWebServerApplicationContext` | 注解读取器 + 类路径扫描器 + 响应式容器 |
| 普通应用 | `AnnotationConfigApplicationContext` | 纯注解配置的标准容器 |

注解驱动上下文构造时内置两个核心工具：

- `AnnotatedBeanDefinitionReader`：读取注解形式的 Bean 定义（配置类、@Bean 方法）
- `ClassPathBeanDefinitionScanner`：扫描指定包路径下的注解 Bean

> 传统 XML 驱动对应 `ClassPathXmlApplicationContext`，内部使用 `XmlBeanDefinitionReader`

## 阶段四：核心 —— 刷新容器（refreshContext）

这是**传统 Spring IoC 容器完整初始化过程**，也是 Bean 发现、解析、注册、实例化的核心阶段。入口是 `AbstractApplicationContext#refresh()`，共 12 步。

### refresh() 标准执行流程

1. `prepareRefresh()`：刷新前准备，设置启动时间、初始化属性源、验证必要属性
2. `obtainFreshBeanFactory()`：获取 `DefaultListableBeanFactory`（XML 驱动模式下会销毁重建；注解驱动的 Spring Boot 中构造时已创建，此处直接返回），准备加载 Bean 定义
3. `prepareBeanFactory()`：对 BeanFactory 做基础配置（类加载器、SPEL 解析器、内置后置处理器、忽略自动装配接口等）
4. `postProcessBeanFactory()`：容器子类扩展点，可继续自定义 BeanFactory
5. `invokeBeanFactoryPostProcessors()`：**执行所有 BeanFactory 后置处理器**，解析配置类、扫描 Bean 的核心步骤
6. `registerBeanPostProcessors()`：注册所有 Bean 后置处理器，用于 Bean 实例化前后增强
7. `initMessageSource()`：初始化国际化消息源
8. `initApplicationEventMulticaster()`：初始化事件广播器
9. `onRefresh()`：容器子类扩展点，**内嵌 Web 服务器在此启动**
10. `registerListeners()`：注册所有应用监听器
11. `finishBeanFactoryInitialization()`：**实例化所有非懒加载单例 Bean**，完成依赖注入
12. `finishRefresh()`：完成刷新，发布上下文刷新完成事件

### Bean 定义加载：注解 vs XML

Bean 加载本质：**读取配置（注解 / XML）→ 解析为统一的 BeanDefinition → 注册到 BeanFactory 的 beanDefinitionMap**。两种方式最终都生成 `BeanDefinition`，仅读取和解析入口不同。

#### （1）注解 Bean 加载流程

注解 Bean 解析主要发生在 `invokeBeanFactoryPostProcessors()` 中，由 `ConfigurationClassPostProcessor` 驱动。

1. **启动配置类解析**
   - Spring Boot 启动类本身就是 `@Configuration` 配置类（注解链路：`@SpringBootApplication` → `@SpringBootConfiguration` → `@Configuration`）
   - `ConfigurationClassPostProcessor` 是 `BeanDefinitionRegistryPostProcessor`，优先执行，负责解析所有 `@Configuration` 配置类
   - 自动配置也在此阶段生效：`@EnableAutoConfiguration` 导入的选择器读取自动配置类列表，这些类本质也是 `@Configuration`，被递归解析
2. **@ComponentScan 组件扫描**
   - 配置类上的 `@ComponentScan` 被 `ConfigurationClassParser` 解析，获取 basePackages
   - 调用 `ClassPathBeanDefinitionScanner#doScan()` 执行扫描：
     1. 包路径转为 `classpath*:com/xxx/**/*.class` 资源路径，定位所有 class 文件
     2. 通过 **ASM 字节码框架**读取 `.class` 元数据，不触发类加载，性能高
     3. 用 `AnnotationTypeFilter` 递归检查类注解：元注解链能追溯到 `@Component`（含 @Service、@Controller、@Repository 等派生注解）即判定为候选组件
     4. 候选类封装为 `ScannedGenericBeanDefinition`，设置作用域、BeanName、懒加载等属性
     5. 注册到 `DefaultListableBeanFactory` 的 `beanDefinitionMap`
3. **@Bean 方法解析**
   - 配置类中 `@Bean` 方法解析为 `ConfigurationClassBeanDefinition`，注册到 BeanFactory
   - 对应 XML 中的 `<bean>` 标签，以 Java 方法形式定义 Bean
4. **@Import、@ImportResource 处理**
   - `@Import`：导入其他配置类，进入递归解析
   - `@ImportResource("classpath:beans.xml")`：导入 XML 配置，衔接 XML Bean 加载

#### （2）XML Bean 加载流程

XML 配置加载核心是 `XmlBeanDefinitionReader`，两种触发场景：

- 传统 Spring：`ClassPathXmlApplicationContext` 构造时直接加载 XML
- Spring Boot：通过 `@ImportResource` 导入 XML 文件，由配置类解析器触发

具体流程：

1. `XmlBeanDefinitionReader` 加载 XML，DOM 模式解析结构
2. 解析 `<beans>` 根标签下各类子标签：
   - **`<bean>` 标签**：解析 id、class、scope、property、constructor-arg 等属性，封装为 `GenericBeanDefinition` 并注册
   - `<context:component-scan base-package="xxx"/>`：与 `@ComponentScan` 完全一致 —— 内部同样创建 `ClassPathBeanDefinitionScanner`，执行相同扫描逻辑
   - 其他命名空间标签（`<aop:config>`、`<tx:annotation-driven>`）：由对应命名空间处理器解析，注册基础设施 Bean
3. 所有 `BeanDefinition` 统一注册到 BeanFactory，与注解扫描出的 Bean 混排在同一 Map，后续生命周期完全一致

#### （3）注解 Bean 与 XML Bean 对比

| 维度 | 注解 Bean（@Component/@Bean） | XML Bean（`<bean>`） |
| --- | --- | --- |
| 配置载体 | Java 类注解 | XML 配置文件 |
| 核心读取器 | ClassPathBeanDefinitionScanner / AnnotatedBeanDefinitionReader | XmlBeanDefinitionReader |
| Bean 发现方式 | 包扫描 + @Component 元注解递归匹配 | XML 标签显式定义 / 配置扫描标签 |
| 错误校验时机 | 编译期可发现类型错误 | 运行期解析才报错 |
| 适合场景 | 业务 Bean、自动配置、快速开发 | 通用配置、第三方组件、历史遗留项目 |

> 本质只是**配置源不同**，最终都抽象为 `BeanDefinition`，进入相同的 Bean 实例化、依赖注入、初始化生命周期。

## 阶段五：Web 容器启动与收尾

### 6. 启动内嵌 Web 服务器

发生在 `refresh()` 的 `onRefresh()` 扩展点中：

- `ServletWebServerApplicationContext` 调用 `createWebServer()`
- 通过 `ServletWebServerFactory`（默认 `TomcatServletWebServerFactory`）创建内嵌 Tomcat 实例
- 初始化 Servlet 上下文，关联 Spring 上下文与 Servlet 上下文
- 注册 `DispatcherServlet`、字符编码过滤器等核心组件
- 绑定端口，启动 Tomcat 监听线程

### 7. 发布启动完成事件

- 容器刷新完成后，发布 `ApplicationStartedEvent`
- 执行所有 `CommandLineRunner`、`ApplicationRunner` 启动回调
- 最终发布 `ApplicationReadyEvent`，标识应用完全启动

# 2、Spring 的注解发现机制

Spring 能自动发现 `@Service`、`@RestController` 等注解，核心是**组件扫描 + 注解派生机制**。简单说：**`@ComponentScan` 告诉 Spring「去哪找、找什么」，`ClassPathBeanDefinitionScanner` 扫描器负责「找到、解析、注册」**。

## 一、前提：这些注解本质都是 `@Component` 的派生

Spring 不为 `@Service`、`@Controller`、`@RestController` 单独实现发现逻辑，它们都是「语义化的 `@Component`」，通过**元注解**方式追溯到 `@Component`：

- `@Service` → 元注解 `@Component`
- `@Controller` → 元注解 `@Component`
- `@RestController` → 元注解 `@Controller` → 元注解 `@Component`
- `@Configuration` → 元注解 `@Component`

`@Service` 源码示例：

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Service {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
```

Spring **递归解析类上所有注解及其元注解**，只要最终能追溯到 `@Component`，就判定为候选组件。

> 自定义注解想被扫描？只要在注解上加 `@Component` 元注解即可。

## 二、`@ComponentScan`：扫描规则说明书

`@ComponentScan` 本身**不直接执行扫描**，它只是一份扫描配置，定义「去哪找、找什么」。

### 核心配置项

| 属性 | 作用 |
| --- | --- |
| `basePackages` / `value` | 扫描根包路径，可指定多个；不写则默认扫描**配置类所在包及其子包** |
| `basePackageClasses` | 以类的方式指定扫描包，类型安全避免写错包名 |
| `includeFilters` | 自定义包含规则，只有符合过滤器的类才注册 |
| `excludeFilters` | 自定义排除规则，符合条件的类即使有 `@Component` 也跳过 |
| `lazyInit` | 扫描到的 Bean 是否默认懒加载 |

> Spring Boot 的 `@SpringBootApplication` 内部已组合 `@ComponentScan` 且未指定 basePackages，所以默认扫描**启动类所在包及其子包** —— 这就是启动类要放在项目根包下的原因。

### 默认匹配规则

`@ComponentScan` 内置一个默认的 `AnnotationTypeFilter`，匹配目标是 `@Component`。所有直接或间接标注了 `@Component` 的类，都会被默认规则命中，不需要额外配置。

## 三、关键时机：扫描什么时候触发

扫描嵌入在 Spring 容器 `refresh()` 的标准流程中，不是加了注解就立刻执行：

1. 容器进入 `invokeBeanFactoryPostProcessors()` 阶段
2. 核心后置处理器 `ConfigurationClassPostProcessor` 开始解析所有 `@Configuration` 配置类
3. 解析配置类注解时，发现类上标注了 `@ComponentScan`
4. 解析 `@ComponentScan` 的所有属性，创建 `ClassPathBeanDefinitionScanner` 扫描器，把过滤器、作用域、懒加载等规则配置进去
5. 调用扫描器的 `doScan()` 方法，正式开始包扫描

> XML 的 `<context:component-scan>` 标签内部也是创建同一个 `ClassPathBeanDefinitionScanner`，执行完全相同的扫描流程。

## 四、核心步骤：`doScan()` 扫描执行流程

整个过程**不触发类加载**，全程通过字节码技术读取元数据，性能很高。

### 步骤 1：包路径 → 类文件资源定位

`ResourcePatternResolver` 把包名转换成资源路径：

- 输入：`com.example.service`
- 转换为：`classpath*:com/example/service/**/*.class`
- 扫描类路径下所有 Jar 包和目录，匹配所有 `.class` 文件

### 步骤 2：ASM 读取类元数据

对每个 `.class` 文件，用 **ASM 字节码框架**直接读取二进制内容，解析出类的注解信息（`AnnotationMetadata`）。

- 全程不触发类加载（不执行静态代码块、不初始化类）
- 可以快速过滤掉不符合条件的类，大幅提升启动速度

### 步骤 3：过滤器匹配（注解识别的核心）

依次执行所有过滤器规则，默认核心是 `AnnotationTypeFilter`，匹配逻辑：

**递归遍历类上所有注解、以及注解的元注解，只要最终能追溯到 `@Component`，就判定匹配成功。**

元注解链示例：

- `@RestController` → `@Controller` → `@Component` ✓
- `@Service` → `@Component` ✓
- `@Configuration` → `@Component` ✓

> 这就是 `@Service`、`@RestController` 不叫 `@Component` 却能被扫描发现的根本原因 —— 它们都是 `@Component` 的「语义化派生注解」。

### 步骤 4：生成 BeanDefinition

匹配成功的类封装为 `ScannedGenericBeanDefinition` —— Bean 的「设计图纸」，包含：

- 类名（`beanClass`）、作用域（singleton/prototype）
- 是否懒加载、自动装配模式
- 初始化方法、销毁方法等所有属性

### 步骤 5：生成 BeanName

默认命名规则：

1. 类名简单名称首字母小写：`UserService` → `userService`
2. 类名前两个字母都是大写则保持原名：`URLService` → `URLService`
3. 注解指定了 value（如 `@Service("userService")`）则使用指定名称

### 步骤 6：注册到 Bean 工厂

`BeanDefinition` 注册到 `DefaultListableBeanFactory` 的 `beanDefinitionMap` 中（以 beanName 为 key 的 Map）。

> 扫描阶段只做「定义注册」，**不做实例化**。实例化发生在 `refresh()` 的 `finishBeanFactoryInitialization()` 阶段，默认只实例化非懒加载的单例 Bean。

## 五、不同注解的功能差异怎么来的

既然都是 `@Component`，`@RestController`、`@Service` 的功能区别来自哪里？

- **扫描阶段**：一视同仁，都按 `@Component` 标准被发现和注册
- **后续生命周期阶段**：由不同基础设施组件根据特定注解做增强
  - `@RestController` / `@Controller`：MVC 基础设施 Bean `RequestMappingHandlerMapping` 在初始化时遍历容器 Bean，解析 `@RequestMapping` 并绑定请求路由
  - `@Service`：**语义化分层标记**，功能上与原生 `@Component` 几乎无差别
  - `@Repository`：由 `PersistenceExceptionTranslationPostProcessor`（Bean 后置处理器）做持久层异常转译增强

## 六、关键总结

1. 发现核心是**容器主动扫描**，不是注解主动注册
2. `@ComponentScan` 是「规则说明书」，`ClassPathBeanDefinitionScanner` 是真正干活的扫描器
3. 识别的核心标记是 `@Component`，其他都是派生的语义化注解，通过**递归元注解链**匹配
4. 底层通过 **ASM 读取字节码**实现，全程不触发类加载，性能高
5. 扫描只做 `BeanDefinition` 注册，不做实例化；实例化在后续 `finishBeanFactoryInitialization` 阶段
6. 不在扫描包范围内的类，加再多注解也不会被发现

# 3、Spring SPI 和 Java SPI 的区别和使用

## 一、概述

Java SPI 和 Spring SPI 本质都是**基于约定的服务发现与扩展机制**，核心作用是不修改框架源码，通过配置文件注册实现类，运行时由框架自动发现并加载，实现面向接口的插拔式扩展。

- **Java SPI**：JDK 原生标准机制，通用性强但功能基础
- **Spring SPI**：Spring 基于 Java SPI 思想自研的增强版，更灵活、性能更优，是 Spring Boot 自动配置、Starter 组件的核心底层

## 二、Java SPI（Service Provider Interface）

### 1. 核心原理与约定

Java SPI 是 JDK 内置服务发现规范，核心实现类 `java.util.ServiceLoader`，遵循「接口 + 配置文件 + 反射加载」模式。

**约定规则：**

1. 定义公共扩展接口
2. 编写接口具体实现类
3. 类路径 `META-INF/services/` 目录下，创建**以接口全限定名为文件名**的配置文件
4. 文件内容为实现类全限定名，每行一个
5. 运行时通过 `ServiceLoader.load(接口.class)` 扫描配置并实例化所有实现

### 2. 使用示例

**步骤 1：定义接口**

```
public interface HelloService {
    String sayHello();
}
```

**步骤 2：编写实现类**

```
public class ChineseHelloService implements HelloService {
    @Override
    public String sayHello() { return "你好"; }
}

public class EnglishHelloService implements HelloService {
    @Override
    public String sayHello() { return "Hello"; }
}
```

**步骤 3：创建 SPI 配置文件**

`resources/META-INF/services/com.example.HelloService`：

```
com.example.ChineseHelloService
com.example.EnglishHelloService
```

**步骤 4：加载并调用**

```
public class JavaSpiDemo {
    public static void main(String[] args) {
        ServiceLoader<HelloService> loader = ServiceLoader.load(HelloService.class);
        for (HelloService service : loader) {
            System.out.println(service.sayHello());
        }
    }
}
```

### 3. 特点与局限

- 优点：JDK 原生无依赖，Java 生态通用扩展标准，跨框架兼容
- 局限：
  1. **无法按需加载**：只能全量遍历实例化所有实现，不能筛选获取指定实现
  2. **不支持排序**：加载顺序由配置文件行顺序决定，无法灵活调整优先级
  3. **扩展点单一**：只能以接口或抽象类作为扩展点，不支持注解类型
  4. **配置分散**：一个接口对应一个配置文件，扩展点多时文件碎片化严重
  5. **实例化能力弱**：只能通过无参构造反射创建，不支持依赖注入

## 三、Spring SPI（Spring Factories 机制）

Spring SPI 官方称为 **Spring Factories**，核心类 `org.springframework.core.io.support.SpringFactoriesLoader`，是对 Java SPI 的深度增强，也是 Spring Boot 自动配置体系的基石。

### 1. 核心原理与约定

**约定规则：**

1. 定义扩展点（可以是接口、抽象类、注解）
2. 编写实现类
3. 类路径 `META-INF/spring.factories` 文件中以 `key=value` 格式配置：
   - key：扩展点全限定名（接口 / 类 / 注解）
   - value：实现类全限定名，多个用英文逗号分隔
4. 运行时通过 `SpringFactoriesLoader` 加载类名或实例化对象

### 2. 使用示例

**步骤 1：定义接口**

```
public interface HelloService {
    String sayHello();
}
```

**步骤 2：编写实现类（支持排序）**

```
@Order(1)
public class ChineseHelloService implements HelloService {
    @Override
    public String sayHello() { return "你好"; }
}

@Order(2)
public class EnglishHelloService implements HelloService {
    @Override
    public String sayHello() { return "Hello"; }
}
```

**步骤 3：创建 spring.factories 配置**

`resources/META-INF/spring.factories`：

```
com.example.HelloService=com.example.ChineseHelloService,com.example.EnglishHelloService
```

**步骤 4：加载并调用**

```
public class SpringSpiDemo {
    public static void main(String[] args) {
        // 方式1：仅加载类名（不实例化，性能开销低）
        List<String> classNames = SpringFactoriesLoader.loadFactoryNames(
            HelloService.class,
            Thread.currentThread().getContextClassLoader()
        );

        // 方式2：加载并实例化（自动按 @Order 排序）
        List<HelloService> services = SpringFactoriesLoader.loadFactories(
            HelloService.class,
            Thread.currentThread().getContextClassLoader()
        );
    }
}
```

### 3. Spring Boot 2.7+ 演进：AutoConfiguration.imports

Spring Boot 2.7 开始，**针对自动配置类**新增更轻量的配置方式，逐步替代 `spring.factories` 中的自动配置条目：

- 文件路径：`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- 文件格式：每行一个自动配置类全限定名
- 适用范围：仅用于 `@AutoConfiguration` 自动配置类；其他扩展点（`ApplicationContextInitializer`、`ApplicationListener` 等）仍使用 `spring.factories`

示例：

```
com.example.autoconfigure.MyAutoConfiguration
```

### 4. 核心增强特性

相比 Java SPI，Spring SPI 做了全方位增强：

1. **类名先行加载**：`loadFactoryNames` 只读取类名不实例化，按需创建对象，启动性能更优
2. **扩展点灵活**：key 支持接口、抽象类、注解（如 `@EnableAutoConfiguration` 作为 key 加载自动配置类）
3. **内置排序能力**：自动识别 `Ordered` 接口和 `@Order` 注解，加载后按优先级排序
4. **配置集中管理**：所有扩展点统一配置在一个 `spring.factories` 文件中，便于维护
5. **Spring 容器深度整合**：SPI 加载的类可交给 IoC 容器管理，支持依赖注入、AOP、生命周期管理
6. **条件装配**：配合 `@ConditionalOnClass`、`@ConditionalOnMissingBean` 等注解，实现按需实例化

## 四、核心区别对比

| 对比维度 | Java SPI | Spring SPI（Spring Factories） |
| --- | --- | --- |
| 规范归属 | JDK 原生标准 | Spring 框架自研 |
| 配置文件 | `META-INF/services/接口全限定名`，一接口一文件 | `META-INF/spring.factories`，所有扩展点集中在一个文件 |
| 配置格式 | 纯文本，每行一个实现类 | Properties 键值对，`key=value1,value2...` |
| 扩展点类型 | 仅支持接口 | 支持接口、抽象类、注解 |
| 加载机制 | `ServiceLoader`，迭代时实例化，必须全量加载 | `SpringFactoriesLoader`，支持仅加载类名，按需实例化 |
| 排序能力 | 原生不支持，仅按配置文件顺序 | 支持 `Ordered` 接口、`@Order` 注解自动排序 |
| 条件过滤 | 原生不支持，只能全量加载 | 可配合条件注解实现按需加载 |
| 实例化方式 | 反射调用无参构造 | 原生无参构造；Spring 环境下可由容器管理，支持依赖注入 |
| 性能 | 全量实例化，扩展点多时启动慢 | 类名预加载，按需实例化，性能更优 |
| 典型应用 | JDBC 驱动、日志门面、Servlet 容器等通用 Java 生态扩展 | Spring Boot 自动配置、Starter 组件、容器扩展、监听器等 Spring 生态扩展 |

# 4、MySQL 驱动如何使用 SPI 的

MySQL 官方驱动（Connector/J）使用 **Java 原生 SPI（ServiceLoader）** 机制，完全遵循 JDBC 4.0 规范的驱动自动发现契约，是 Java SPI 最经典的工业级应用场景。

## 一、JDBC 的 SPI 扩展契约

JDBC 4.0（JDK 6 起）引入基于 SPI 的驱动自动注册机制，替代早期手动 `Class.forName()` 加载方式。

- **SPI 扩展接口**：`java.sql.Driver` —— 所有数据库驱动必须实现此接口，提供连接创建、URL 匹配能力
- **配置约定**：驱动 Jar 包必须在 `META-INF/services/` 目录下创建名为 `java.sql.Driver` 的文件，内容为驱动实现类全限定名
- **加载入口**：`java.sql.DriverManager` 初始化时，通过 `ServiceLoader` 自动扫描类路径下所有符合约定的驱动实现并完成注册

## 二、MySQL 驱动中的 SPI 实现

以 MySQL Connector/J 8.x 为例：

### 1. SPI 配置文件

解压 `mysql-connector-java-8.0.x.jar`，找到约定文件：`META-INF/services/java.sql.Driver`

文件内容：

```
com.mysql.cj.jdbc.Driver
```

> 版本差异：
> - MySQL 5.x 驱动：`com.mysql.jdbc.Driver`
> - MySQL 8.x 驱动：主驱动类重构为 `com.mysql.cj.jdbc.Driver`，同时保留旧 `com.mysql.jdbc.Driver` 作为兼容空类（继承自新驱动类）

### 2. 驱动类的自注册逻辑

`com.mysql.cj.jdbc.Driver` 核心源码简化：

```
package com.mysql.cj.jdbc;

import java.sql.SQLException;

public class Driver extends NonRegisteringDriver implements java.sql.Driver {
    static {
        try {
            java.sql.DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            throw new RuntimeException("Can't register MySQL driver!", e);
        }
    }

    public Driver() throws SQLException {
    }
}
```

关键逻辑：

- SPI 的 `ServiceLoader` 通过反射调用无参构造器，实例化 `Driver` 类
- 类加载时**静态代码块自动执行**，调用 `DriverManager.registerDriver()` 完成驱动自注册

## 三、完整执行流程

执行 `DriverManager.getConnection("jdbc:mysql://localhost:3306/db", "root", "pwd")` 时，全链路如下：

1. **DriverManager 初始化**：首次使用时执行静态代码块，调用 `loadInitialDrivers()`
2. **SPI 扫描驱动实现**：`loadInitialDrivers()` 内部创建 `ServiceLoader` 执行扫描：
   ```
   ServiceLoader<java.sql.Driver> loadedDrivers = ServiceLoader.load(java.sql.Driver.class);
   Iterator<java.sql.Driver> driversIterator = loadedDrivers.iterator();
   ```
   遍历类路径下所有 Jar 包的 `META-INF/services/java.sql.Driver` 文件，收集驱动实现类名
3. **实例化并注册驱动**：
   - 遍历驱动类名，反射实例化 `com.mysql.cj.jdbc.Driver`
   - 实例化触发静态代码块执行，将驱动实例注册到 `DriverManager` 的 `registeredDrivers` 全局列表
4. **匹配驱动并创建连接**：调用 `getConnection()` 时，`DriverManager` 遍历所有已注册驱动：
   - 调用每个驱动的 `acceptsURL(url)` 方法，判断能否处理该 JDBC URL
   - MySQL 驱动匹配 `jdbc:mysql:` 前缀的 URL
   - 匹配成功后，调用驱动的 `connect()` 方法创建数据库连接并返回

## 四、多驱动共存：原生支持但不推荐

**技术上 JDBC 规范 + Java SPI 原生支持多驱动共存**，但业务场景不推荐，会引发类加载冲突、驱动选择不可控、隐性兼容 bug 等问题。

### 1. 为什么原生支持

**（1）SPI 机制原生支持多实现**

- 每个驱动 Jar 都有独立的 `META-INF/services/java.sql.Driver` 配置文件
- `ServiceLoader` 扫描类路径下所有 Jar 中的该文件，收集全部实现类，逐个实例化并注册
- 无论哪个厂商、哪个版本，只要实现 `java.sql.Driver` 接口就能被加载

**（2）DriverManager 支持多驱动注册**

- `DriverManager` 内部维护 `registeredDrivers` 列表（`CopyOnWriteArrayList`），使用 `addIfAbsent()` 按对象引用去重，但同一驱动类被不同类加载器加载或多次实例化时仍会重复注册
- 调用 `getConnection(url)` 时按注册顺序遍历所有驱动：
  1. 调用 `boolean acceptsURL(String url)` 判断能否处理该 URL
  2. 第一个匹配成功的驱动用于创建连接并返回

### 2. 多驱动共存的风险

**（1）类加载冲突：同名类覆盖，版本不可控**

同厂商多版本场景下的核心问题：

- MySQL 5.x 主类：`com.mysql.jdbc.Driver`
- MySQL 8.x 主类：`com.mysql.cj.jdbc.Driver`，同时保留 `com.mysql.jdbc.Driver` 作为兼容空类

同时引入 5.1.x 和 8.0.x 两个 Jar：

- 同一类加载器下 `com.mysql.jdbc.Driver` 只能加载一个版本，具体哪个取决于 ClassPath 中 Jar 包顺序，先扫描到的优先加载
- 运行时驱动版本不可控，代码预期与实际运行可能不一致
- 极端情况抛出 `LinkageError`、`NoClassDefFoundError`：加载了 5.x 的 Driver 类，但依赖的其他类来自 8.x 的 Jar，版本不匹配导致类验证失败

**（2）驱动匹配顺序不可控，连接行为异常**

所有 MySQL 兼容驱动都能匹配 `jdbc:mysql://` 前缀 URL，`DriverManager` 按注册顺序返回第一个匹配的驱动：

- 注册顺序 = SPI 扫描顺序 = ClassPath 中 Jar 包排序，开发者无法通过业务代码控制
- 预期使用 8.x 驱动的 SSL、时区等特性，实际可能调用 5.x 老驱动，导致参数不兼容、连接报错、数据异常（时间差 8 小时、字符集乱码）
- 这类问题表现为隐性行为异常，排查成本高

**（3）重复注册与资源泄漏**

- `DriverManager` 无去重逻辑，同一驱动类被不同类加载器加载或多次实例化注册，列表中出现重复条目，遍历匹配产生不必要性能开销
- 驱动卸载困难：多版本混存时，`deregisterDriver()` 只能卸载一个实例，剩余驱动的静态资源（线程、定时器、缓存、JMX MBean）无法释放，引发内存泄漏

**（4）连接池与框架兼容性问题**

主流连接池（HikariCP、Druid）和 ORM 框架（MyBatis、Spring Data JPA）都有驱动自动推断逻辑：

- ClassPath 下有多个驱动，框架可能推断到错误的驱动类或版本
- 配置中 `driver-class-name` 写旧类名 `com.mysql.jdbc.Driver`，实际加载的可能是另一个版本实现，导致连接参数失效、连接池初始化失败
- Spring Boot 自动配置同样受影响：根据类路径驱动类自动匹配数据源配置，多驱动共存时可能配置不生效、启动报错

**（5）特性不兼容与隐性业务 Bug**

不同版本 MySQL 驱动核心行为存在大量差异：

- 时间类型处理：8.x 默认启用服务器时区校验，5.x 不处理，混用导致时间偏移
- SSL 行为：8.x 默认开启 SSL 连接，5.x 默认关闭，混用导致连接失败或安全风险
- 结果集、事务、预编译语句的行为细节也存在版本差异

这些问题不导致连接失败，但产生数据错误、事务异常等业务 Bug，排查难度远高于直接报错。
