## 1. Dubbo的SPI和JDK的SPI有什么区别？

**JDK SPI** 的不足：
- 会一次性**实例化扩展点所有实现**，如果某些扩展初始化耗时但又用不上，浪费资源
- 配置文件中只列出所有扩展实现，**没有命名**，很难准确引用需要的实现
- **不支持IoC和AOP**，扩展之间无法自动注入依赖
- 扩展**很难与Spring等其他框架集成**

**Dubbo SPI** 的改进：
- **按需加载**，通过扩展名（如 `random`）精确获取特定实现，不会一次性加载全部
- 扩展点有**别名**（`配置名=实现类全限定名`），方便在配置中引用
- 支持**IoC**：查找setter方法，通过**AdaptiveExtensionFactory**遍历SpiExtensionFactory和SpringExtensionFactory注入依赖
- 支持**AOP**：通过**装饰者模式**的Wrapper类（如ProtocolFilterWrapper）在扩展点执行前后插入通用逻辑
- 配置文件位于 `META-INF/dubbo/接口全限定名`，格式为 `key=实现类`

加载流程：ExtensionLoader按需加载 → 反射创建实例 → setter方法依赖注入（IoC） → Wrapper类包装（AOP）。

## 2. Dubbo如何实现服务降级？

Dubbo通过**mock**配置实现服务降级，不发起远程调用或调用失败时返回兜底数据：

- **mock=force:return+null**（屏蔽）：消费方对该服务的方法调用**直接返回null，不发起远程调用**。用于屏蔽不重要服务不可用时对调用方的影响
- **mock=fail:return+null**（容错）：消费方对该服务的方法调用在失败后**返回null，不抛异常**。用于容忍不重要服务不稳定时对调用方的影响

此外也支持配置**mock类**（实现服务接口），在调用失败时执行自定义的降级逻辑，返回友好的缺省值。

## 3. Dubbo接口设计有哪些原则？

- **接口粒度适中**：过大会导致接口职责不单一，过小会增加服务间调用次数。按业务模块划分，每个接口对应一个业务领域
- **参数使用POJO**：尽量使用POJO对象而非Map等松散结构，便于传参和版本兼容
- **接口版本管理**：通过`version`属性管理接口版本，支持多版本共存，便于向后兼容升级
- **结果使用POJO**：统一返回DTO对象，避免返回JSONObject等无类型结构
- **服务拆分原则**：遵循**单一职责**，将功能内聚性强的逻辑放在同一服务中，避免跨服务频繁调用

## 4. Dubbo标签是如何解析的？

Dubbo基于**Spring的XML Schema扩展机制**解析自定义标签：

1. 在 `META-INF/spring.schemas` 中配置XSD文件路径，定义Dubbo XML标签的语法规则
2. 在 `META-INF/spring.handlers` 中配置 `DubboNamespaceHandler`，将指定命名空间的标签映射到处理器
3. **DubboNamespaceHandler** 继承 `NamespaceHandlerSupport`，注册各标签对应的 **DubboBeanDefinitionParser**
4. **DubboBeanDefinitionParser** 解析 `<dubbo:service/>`、`<dubbo:reference/>`、`<dubbo:protocol/>`、`<dubbo:registry/>` 等标签
5. 解析结果转化为对应的Spring BeanDefinition，注册到容器中（如ServiceBean、ReferenceBean）

## 5. Dubbo如何实现多版本控制？

Dubbo支持**多版本控制**，通过 **version** 属性配置服务版本号，实现平滑升级和灰度发布：

- **服务提供者**：在 `@Service` 或 `<dubbo:service/>` 中指定 `version`，可以同时暴露多个版本的服务
- **服务消费者**：在 `@Reference` 或 `<dubbo:reference/>` 中指定 `version`，调用特定版本的服务
- **不区分版本**：设置 `version=*`，表示调用任意可用版本的服务
- **应用场景**：接口升级时，旧版本和新版本服务同时存在，消费者逐步切换到新版本，实现**灰度发布**，验证无误后再下线旧版本

## 6. 服务提供者如何实现失效踢出？

失效踢出基于**ZooKeeper的临时节点**和**Watcher机制**：

1. 服务提供者启动时，在ZK注册中心的 **`/dubbo/interface/providers`** 路径下创建**临时节点**，节点中包含IP、端口等元数据
2. 提供者与ZK维持**长连接心跳**（Session），会话有效则临时节点存活
3. **主动下线**：提供者正常关闭时断开ZK连接，ZK删除临时节点
4. **异常下线**：提供者宕机或网络中断，ZK检测到会话超时，自动删除临时节点
5. 消费者通过 **Watcher** 监听 `providers` 节点变化，收到通知后**更新本地缓存的服务地址列表**，实现失效自动踢出和动态感知

## 7. Dubbo的异常处理机制是怎样的？

Dubbo通过 **ExceptionFilter** 处理远程调用中的异常序列化和传递问题：

- **Checked异常**：提供者抛出的checked异常会**直接序列化**后传递给消费者，消费者可以正常catch
- **Unchecked异常**（RuntimeException）：ExceptionFilter捕获后，如果异常在**提供者和消费者接口中都有声明**（throws），则序列化传递；否则包装为 **RuntimeException**，消费者端收到未预期的异常
- **自定义异常**：需实现 **Serializable** 接口，并提供**全参构造方法**；提供者和消费者两端必须包含**完全相同的异常类**（全限定名一致），否则反序列化失败
- **异常处理建议**：服务接口方法抛出明确的业务异常，避免直接抛RuntimeException；消费者端做**容错处理**（try-catch或mock降级）
