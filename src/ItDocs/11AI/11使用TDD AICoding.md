# 在AI coding场景下，可以使用TDD的方式开发代码，但是对于某些依赖其他服务或者数据或者资源的代码，怎么进行单元测试

可以，而且这其实是 **AI Coding 场景下 TDD 最值得强调的一点**：

> **单元测试不是要求“完全不依赖外部东西”，而是要求测试时可以控制依赖。**

对于依赖数据库、HTTP 服务、Redis、MQ、文件、第三方 API、模型服务等代码，一般不要直接把真实依赖拉进单元测试，而是把代码拆成：

**业务逻辑 + 依赖抽象 + 可替换实现**

例如：

```text
Controller
   ↓
Application Service
   ↓
Domain / Business Logic
   ↓
Repository / Client / Gateway
   ↓
MySQL / Redis / HTTP / MQ / LLM

```

在 TDD 中重点测试中间这一层。

---

# 1. 最核心的方法：依赖倒置 + Mock/Fake

例如你有一个订单服务：

```java
public class OrderService {

    private final UserRepository userRepository;
    private final PaymentClient paymentClient;

    public OrderService(
            UserRepository userRepository,
            PaymentClient paymentClient) {
        this.userRepository = userRepository;
        this.paymentClient = paymentClient;
    }

    public OrderResult createOrder(Long userId, BigDecimal amount) {

        User user = userRepository.findById(userId);

        if (user == null) {
            throw new UserNotFoundException();
        }

        PaymentResult result = paymentClient.pay(userId, amount);

        if (!result.success()) {
            throw new PaymentFailedException();
        }

        return new OrderResult(true);
    }
}

```

这里真正值得测试的是：

```text
用户不存在 → 抛异常

用户存在 + 支付成功 → 创建成功

用户存在 + 支付失败 → 创建失败

```

而不是在单元测试里面真的：

```text
访问 MySQL
调用支付宝
调用 HTTP 服务

```

测试可以写成：

```java
@Test
void should_create_order_when_payment_success() {

    UserRepository userRepository = mock(UserRepository.class);
    PaymentClient paymentClient = mock(PaymentClient.class);

    when(userRepository.findById(1L))
            .thenReturn(new User(1L));

    when(paymentClient.pay(1L, new BigDecimal("100")))
            .thenReturn(PaymentResult.success());

    OrderService service =
            new OrderService(userRepository, paymentClient);

    OrderResult result =
            service.createOrder(1L, new BigDecimal("100"));

    assertTrue(result.success());
}

```

这里：

```text
OrderService      ← 真代码
UserRepository    ← Mock
PaymentClient     ← Mock

```

这样就可以高速执行大量 TDD 测试。

---

# 2. 但 AI Coding 有一个特别重要的问题

传统开发很多时候是：

```text
人写代码
↓
人写测试
↓
运行测试
↓
修 Bug

```

AI Coding 更容易变成：

```text
需求
↓
AI 写代码
↓
AI 写测试
↓
测试全部通过
↓
以为代码正确

```

这里有一个很大的风险：

> **AI 很可能把测试写成“配合实现通过”，而不是验证真实行为。**

所以 AI Coding 中，依赖外部资源的测试最好采用：

```text
Unit Test
+
Contract Test
+
Integration Test
+
E2E Test

```

而不是所有东西都 Mock。

---

# 3. 我比较推荐的测试分层

可以理解成四层。

```text
                    E2E
                 /        \
          Integration    Contract
               \            /
                Unit Test

```

具体来说：

### 第一层：Unit Test

不访问真实外部资源。

例如：

```text
数据库       Mock
Redis        Mock
HTTP API     Mock
MQ           Mock
文件系统     Fake
LLM          Mock
时间         Fake Clock
随机数       固定

```

主要测试：

```text
业务规则
边界条件
异常处理
状态转换
算法
数据转换

```

特点：

```text
快
多
稳定

```

例如一个类写了 20 个方法，可以有几十到几百个 Unit Test。

---

# 4. 第二层：Integration Test

这个时候才真正连接基础设施。

例如：

```text
OrderService
    ↓
MySQL

```

测试：

```java
@SpringBootTest
@Testcontainers
class OrderRepositoryTest {
}

```

例如启动：

```text
Docker MySQL
Docker Redis
Docker Kafka

```

然后测试：

```text
insert
update
delete
transaction
index
SQL
serialization

```

比如：

```text
OrderService
    ↓
OrderRepository
    ↓
MySQL

```

这个时候不应该 Mock Repository。

因为：

> **你真正想验证的是 Repository 到 MySQL 的行为。**

---

# 5. 第三层：Contract Test

这个对于 **微服务 / AI Agent / HTTP API** 特别重要。

假设：

```text
Agent Service
      ↓
User Service

```

User Service API：

```http
GET /users/{id}

```

返回：

```json
{
  "id": 123,
  "name": "Jack",
  "status": "ACTIVE"
}

```

Agent Service 单元测试：

```text
UserClient → Mock

```

但是你怎么保证：

```text
User Service

```

升级之后不会把：

```json
status

```

改成：

```json
userStatus

```

导致 Agent Service 崩掉？

这时候需要 Contract Test。

比如：

```text
Producer
User Service
     ↓
Contract

Consumer
Agent Service

```

规定：

```json
{
  "id": integer,
  "name": string,
  "status": string
}

```

只要 API 契约变化：

```text
CI
 ↓
Contract Test
 ↓
失败

```

这样就不会完全依赖 Mock。

---

# 6. 第四层：E2E

最后才是真实环境。

例如：

```text
Browser
   ↓
API
   ↓
Agent
   ↓
RAG
   ↓
Vector DB
   ↓
LLM
   ↓
Tool
   ↓
Database

```

真正跑一条完整链路：

```text
用户提出问题
        ↓
Agent
        ↓
检索
        ↓
工具调用
        ↓
模型
        ↓
最终回答

```

这种测试数量不要太多。

因为：

```text
慢
贵
不稳定
难定位

```

---

# 7. 对“依赖数据”的代码怎么办？

这个问题非常常见。

例如：

```java
public RiskResult checkRisk(User user) {
    RiskRule rule = riskRuleRepository.findRule(user.getLevel());

    ...
}

```

这里依赖数据库中的：

```text
risk_rule

```

不要在 Unit Test 中直接依赖真实数据库。

可以构造：

```java
FakeRiskRuleRepository

```

例如：

```java
class FakeRiskRuleRepository
        implements RiskRuleRepository {

    private final Map<String, RiskRule> rules = Map.of(
        "VIP",
        new RiskRule("VIP", 100)
    );

    @Override
    public RiskRule findRule(String level) {
        return rules.get(level);
    }
}

```

测试：

```java
@Test
void vip_user_should_pass() {

    RiskRuleRepository repository =
            new FakeRiskRuleRepository();

    RiskService service =
            new RiskService(repository);

    ...
}

```

这个其实往往比 Mock 更好。

---

# 8. Mock 和 Fake 不要混为一谈

我比较推荐：

```text
简单行为 → Mock
复杂数据行为 → Fake
真实基础设施 → Integration Test

```

例如：

### Mock

```java
when(userRepository.findById(1L))
    .thenReturn(user);

```

适合：

```text
验证某个方法有没有调用
返回某个异常
返回某个结果

```

---

### Fake

例如：

```java
FakeUserRepository
FakeCache
FakeFileStorage
FakeClock

```

适合：

```text
有一定业务行为
需要保存状态
测试数据比较复杂

```

例如：

```java
class FakeOrderRepository
        implements OrderRepository {

    private final Map<Long, Order> orders =
            new HashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Order findById(Long id) {
        return orders.get(id);
    }
}

```

这实际上非常适合 AI Coding。

---

# 9. 对 HTTP 服务尤其重要

例如：

```java
OpenAiClient

```

你不应该每执行一次测试就真的调用模型。

否则：

```text
1000 个测试
×
真实 API
×
token

```

成本会非常高。

Unit Test：

```java
when(llmClient.chat(any()))
        .thenReturn("""
            {"risk":"HIGH"}
        """);

```

测试：

```text
LLM 返回 HIGH
→ 风控拒绝

LLM 返回 LOW
→ 风控通过

LLM 返回非法 JSON
→ fallback

LLM timeout
→ retry

LLM 500
→ 熔断

```

这些都可以非常稳定地测试。

---

# 10. 但 AI Agent 有一个特殊问题

你最近如果是在做 AI Agent，这一点尤其重要。

Agent 通常是：

```text
LLM
 ↓
Tool
 ↓
DB
 ↓
Search
 ↓
LLM

```

如果全 Mock：

```text
LLM Mock
Tool Mock
DB Mock
Search Mock

```

测试可能全部通过。

但是实际上：

```text
Prompt
Tool Schema
JSON
Context
Memory
模型输出

```

之间可能根本接不上。

所以 AI Agent 我会建议：

```text
             ┌──────────────┐
             │  Unit Test   │
             └──────┬───────┘
                    │
        ┌───────────┼───────────┐
        ↓           ↓           ↓
      LLM Mock    Tool Fake   DB Mock


             ┌──────────────┐
             │ Integration  │
             └──────┬───────┘
                    ↓
            Real Tool / DB


             ┌──────────────┐
             │ Eval / E2E   │
             └──────┬───────┘
                    ↓
             Real LLM

```

尤其增加一个：

> **LLM Eval**

例如准备：

```json
[
  {
    "question": "xxx",
    "expected_tool": "search_case",
    "expected_result": "..."
  }
]

```

然后评测：

```text
Tool 是否调用正确
参数是否正确
引用是否正确
最终答案是否符合预期

```

这比传统 Unit Test 更适合 AI Agent。

---

# 11. 对外部资源可以建立“测试替身”

这是非常适合 AI Coding 的设计。

比如：

```text
真实实现                 测试实现

MySQLRepository    →    InMemoryRepository
RedisCache         →    FakeRedis
HttpClient         →    MockHttpClient
FileStorage        →    InMemoryStorage
LLMClient          →    FakeLLM
Clock              →    FixedClock
UUID               →    FixedUUIDGenerator

```

代码：

```java
public interface Clock {
    Instant now();
}

```

生产：

```java
class SystemClock implements Clock {

    public Instant now() {
        return Instant.now();
    }
}

```

测试：

```java
class FixedClock implements Clock {

    public Instant now() {
        return Instant.parse(
            "2026-08-14T10:00:00Z"
        );
    }
}

```

这样：

```text
昨天
今天
明天
月底
跨年

```

都可以测试。

这类设计对于 TDD 非常关键。

---

# 12. 最关键的是：不要把“依赖”写死

这是 TDD 最容易暴露的架构问题。

比如这种代码：

```java
public void createOrder() {

    MySqlOrderRepository repository =
        new MySqlOrderRepository();

    RedisTemplate redis =
        new RedisTemplate();

    ...
}

```

这基本很难测试。

更好的写法：

```java
public class OrderService {

    private final OrderRepository repository;
    private final Cache cache;

    public OrderService(
        OrderRepository repository,
        Cache cache) {

        this.repository = repository;
        this.cache = cache;
    }
}

```

于是：

```text
Production

OrderService
   ↓
MySqlOrderRepository
   ↓
MySQL

```

测试：

```text
OrderService
   ↓
FakeOrderRepository

```

---

# 13. AI Coding 可以进一步做成“测试驱动 Agent”

这个思路其实很有意思。

你可以让 AI Coding 遵循：

```text
需求
 ↓
AI 提取验收标准
 ↓
AI 生成 Test
 ↓
Test FAIL
 ↓
AI 写代码
 ↓
Test PASS
 ↓
AI 重构
 ↓
Test PASS

```

例如需求：

> 用户连续登录 5 次失败后锁定账户 30 分钟。

AI 先生成：

```java
@Test
void should_lock_after_5_failed_attempts()

@Test
void should_not_lock_after_4_failed_attempts()

@Test
void should_unlock_after_30_minutes()

@Test
void successful_login_should_reset_failed_count()

```

然后再实现：

```text
AccountService

```

这样就比较接近真正的：

> **Specification → Test → Implementation**

而不是：

> Prompt → Code

---

# 14. 我建议你在 AI Coding 中采用这个原则

可以简单记成：

```text
                ┌───────────────┐
                │     E2E       │
                │   少量即可     │
                └───────▲───────┘
                        │
                ┌───────┴───────┐
                │ Integration   │
                │ 真实DB/HTTP等  │
                └───────▲───────┘
                        │
                ┌───────┴───────┐
                │ Contract Test │
                │ API/Schema契约 │
                └───────▲───────┘
                        │
                ┌───────┴───────┐
                │   Unit Test   │
                │ Mock / Fake   │
                └───────────────┘

```

比例可以大致控制成：

```text
Unit        70~80%
Integration 15~20%
Contract     5~10%
E2E          少量

```

并且有一个非常重要的判断：

> **一个测试如果因为“外部服务不可用”而无法运行，它通常就不应该属于 Unit Test。**

它应该被放到 Integration / Contract / E2E 层。

---

# 15. 对 AI Coding，我甚至建议增加一个“依赖测试策略”

让 AI 在写代码前先识别：

```text
这段代码依赖什么？

```

例如：

```text
Dependencies
├── Database
├── Redis
├── HTTP
├── MQ
├── File
├── Clock
├── Random
└── LLM

```

然后 AI 自动生成：

```text
Database → Repository interface + Fake
Redis    → Cache interface + Fake
HTTP     → Client interface + Mock Server
MQ       → Producer interface + Fake
File     → Storage interface + InMemory
Clock    → Clock interface + FixedClock
Random   → RandomProvider
LLM      → LLM interface + FakeLLM

```

再生成对应测试。

这样就能把：

> **“AI Coding + TDD”**

真正变成一种工程方法，而不是单纯让 AI “顺手写几个 JUnit”。

对于你这种 **Java/Spring Boot 后端 + AI Agent/大模型应用** 的场景，我会尤其推荐 **“Unit Test + Testcontainers Integration Test + Contract Test + LLM Eval”** 这套组合。它比纯 Mock 测试体系可靠得多。


# 对于依赖其他内部方法，但是又是private的怎么办


这种情况其实是 **TDD 里非常典型的问题**。核心原则是：

> **不要为了测试 private 方法而专门测试 private 方法。**

应该测试 public 方法产生的**可观察行为**。

例如：

```java
public class OrderService {

    public OrderResult createOrder(Order order) {
        validate(order);
        BigDecimal amount = calculateAmount(order);
        return save(order, amount);
    }

    private void validate(Order order) {
        ...
    }

    private BigDecimal calculateAmount(Order order) {
        ...
    }

    private OrderResult save(Order order, BigDecimal amount) {
        ...
    }
}

```

通常测试：

```java
@Test
void should_create_order_successfully() {
    OrderResult result = service.createOrder(order);

    assertThat(result.isSuccess()).isTrue();
}

```

而不是：

```java
// 不推荐
invokePrivateMethod("validate");
invokePrivateMethod("calculateAmount");

```

因为 private 方法属于**实现细节**，测试它会导致一个问题：

```text
修改内部实现
    ↓
业务行为完全没变
    ↓
大量测试却失败

```

这会让测试变得很脆弱。

---

## 但有一种情况很重要

假设 private 方法本身已经非常复杂：

```java
public void processRisk(Request request) {

    RiskContext context = buildContext(request);

    RiskResult result = evaluateRules(context);

    saveResult(result);
}

private RiskContext buildContext(Request request) {
    // 100 行
}

private RiskResult evaluateRules(RiskContext context) {
    // 300 行复杂规则
}

```

这时候你可能会产生疑问：

> `evaluateRules()` 这么复杂，我如果只能通过 `processRisk()` 测，测试是不是很麻烦？

这时候我的建议不是：

```text
把 private 改成 public

```

而是：

```text
把复杂职责提取成独立对象

```

例如：

```java
public class RiskRuleEvaluator {

    public RiskResult evaluate(RiskContext context) {
        ...
    }
}

```

然后：

```java
public class RiskService {

    private final RiskRuleEvaluator evaluator;

    public RiskResult process(Request request) {

        RiskContext context = buildContext(request);

        return evaluator.evaluate(context);
    }

    private RiskContext buildContext(Request request) {
        ...
    }
}

```

于是测试结构变成：

```text
RiskServiceTest
    ↓
测试整体流程

RiskRuleEvaluatorTest
    ↓
测试复杂规则

```

这就非常适合 TDD。

---

# 一个判断标准

看到 private 方法时，可以问自己：

> **这个 private 方法是否包含独立的业务概念？**

如果只是：

```java
private String trim(String value)

```

这种简单辅助逻辑：

```text
不要拆
不要单独测
通过 public 方法覆盖

```

如果是：

```java
private RiskResult evaluateRisk(...)
private PricingResult calculatePrice(...)
private PermissionResult checkPermission(...)

```

而且：

```text
逻辑复杂
有多个分支
有独立业务规则
容易单独演进

```

那么通常说明：

> **它可能已经不是一个“方法”，而是一个应该独立存在的业务组件。**

---

# 这对 AI Coding 特别重要

你可以给 AI 一个规则：

```text
生成代码时遵循：

1. 不直接为 private 方法生成单独测试
2. 优先通过 public API 验证行为
3. 如果 private 方法复杂且需要大量单独测试，
   优先考虑提取为独立类/组件
4. 不要为了测试方便降低 private 的封装级别

```

例如 AI 看到：

```java
private RiskDecision calculateRisk(...)

```

不要让它生成：

```java
ReflectionTestUtils.invokeMethod(...)

```

而应该让它判断：

```text
calculateRisk 是否是独立的业务能力？
       ↓
      是
       ↓
提取 RiskCalculator
       ↓
RiskCalculatorTest

```

---

# Spring 项目里还有一个常见误区

很多人会因为测试困难，把：

```java
private

```

改成：

```java
public

```

或者：

```java
package-private

```

只为了测试。

我一般不建议。

因为：

> **测试应该适应合理的设计，而不是为了测试破坏封装。**

`ReflectionTestUtils` 可以用，但我会把它当成**最后手段**，比如遗留代码、无法重构的第三方集成代码，而不是正常 TDD 的主流方案。

---

## 最终可以形成一个很简单的决策树

```text
这个 private 方法需要测试？
        │
        ▼
是否可以通过 public 方法验证？
        │
   ┌────┴────┐
   是        否
   │          │
   ▼          ▼
直接测      private 是否复杂？
public        │
方法       ┌──┴──┐
           否    是
           │      │
           ▼      ▼
       重构设计   提取独立类
                  │
                  ▼
             单独测试新类

```

所以在 **AI Coding + TDD** 里，一个非常好的信号其实是：

> **“这个 private 方法很难测”往往不是测试问题，而是设计问题。**

尤其当一个 private 方法需要你准备大量 Mock、数据和各种分支才能覆盖时，往往已经在提示你：**这个职责应该被拆出来了。**


这种情况其实是 **TDD 里非常典型的问题**。核心原则是：

> **不要为了测试 private 方法而专门测试 private 方法。**

应该测试 public 方法产生的**可观察行为**。

例如：

```java
public class OrderService {

    public OrderResult createOrder(Order order) {
        validate(order);
        BigDecimal amount = calculateAmount(order);
        return save(order, amount);
    }

    private void validate(Order order) {
        ...
    }

    private BigDecimal calculateAmount(Order order) {
        ...
    }

    private OrderResult save(Order order, BigDecimal amount) {
        ...
    }
}

```

通常测试：

```java
@Test
void should_create_order_successfully() {
    OrderResult result = service.createOrder(order);

    assertThat(result.isSuccess()).isTrue();
}

```

而不是：

```java
// 不推荐
invokePrivateMethod("validate");
invokePrivateMethod("calculateAmount");

```

因为 private 方法属于**实现细节**，测试它会导致一个问题：

```text
修改内部实现
    ↓
业务行为完全没变
    ↓
大量测试却失败

```

这会让测试变得很脆弱。

---

## 但有一种情况很重要

假设 private 方法本身已经非常复杂：

```java
public void processRisk(Request request) {

    RiskContext context = buildContext(request);

    RiskResult result = evaluateRules(context);

    saveResult(result);
}

private RiskContext buildContext(Request request) {
    // 100 行
}

private RiskResult evaluateRules(RiskContext context) {
    // 300 行复杂规则
}

```

这时候你可能会产生疑问：

> `evaluateRules()` 这么复杂，我如果只能通过 `processRisk()` 测，测试是不是很麻烦？

这时候我的建议不是：

```text
把 private 改成 public

```

而是：

```text
把复杂职责提取成独立对象

```

例如：

```java
public class RiskRuleEvaluator {

    public RiskResult evaluate(RiskContext context) {
        ...
    }
}

```

然后：

```java
public class RiskService {

    private final RiskRuleEvaluator evaluator;

    public RiskResult process(Request request) {

        RiskContext context = buildContext(request);

        return evaluator.evaluate(context);
    }

    private RiskContext buildContext(Request request) {
        ...
    }
}

```

于是测试结构变成：

```text
RiskServiceTest
    ↓
测试整体流程

RiskRuleEvaluatorTest
    ↓
测试复杂规则

```

这就非常适合 TDD。

---

# 一个判断标准

看到 private 方法时，可以问自己：

> **这个 private 方法是否包含独立的业务概念？**

如果只是：

```java
private String trim(String value)

```

这种简单辅助逻辑：

```text
不要拆
不要单独测
通过 public 方法覆盖

```

如果是：

```java
private RiskResult evaluateRisk(...)
private PricingResult calculatePrice(...)
private PermissionResult checkPermission(...)

```

而且：

```text
逻辑复杂
有多个分支
有独立业务规则
容易单独演进

```

那么通常说明：

> **它可能已经不是一个“方法”，而是一个应该独立存在的业务组件。**

---

# 这对 AI Coding 特别重要

你可以给 AI 一个规则：

```text
生成代码时遵循：

1. 不直接为 private 方法生成单独测试
2. 优先通过 public API 验证行为
3. 如果 private 方法复杂且需要大量单独测试，
   优先考虑提取为独立类/组件
4. 不要为了测试方便降低 private 的封装级别

```

例如 AI 看到：

```java
private RiskDecision calculateRisk(...)

```

不要让它生成：

```java
ReflectionTestUtils.invokeMethod(...)

```

而应该让它判断：

```text
calculateRisk 是否是独立的业务能力？
       ↓
      是
       ↓
提取 RiskCalculator
       ↓
RiskCalculatorTest

```

---

# Spring 项目里还有一个常见误区

很多人会因为测试困难，把：

```java
private

```

改成：

```java
public

```

或者：

```java
package-private

```

只为了测试。


我一般不建议。

因为：

> **测试应该适应合理的设计，而不是为了测试破坏封装。**

`ReflectionTestUtils` 可以用，但我会把它当成**最后手段**，比如遗留代码、无法重构的第三方集成代码，而不是正常 TDD 的主流方案。

---

## 最终可以形成一个很简单的决策树

```text
这个 private 方法需要测试？
        │
        ▼
是否可以通过 public 方法验证？
        │
   ┌────┴────┐
   是        否
   │          │
   ▼          ▼
直接测      private 是否复杂？
public        │
方法       ┌──┴──┐
           否    是
           │      │
           ▼      ▼
       重构设计   提取独立类
                  │
                  ▼
             单独测试新类

```

所以在 **AI Coding + TDD** 里，一个非常好的信号其实是：

> **“这个 private 方法很难测”往往不是测试问题，而是设计问题。**

尤其当一个 private 方法需要你准备大量 Mock、数据和各种分支才能覆盖时，往往已经在提示你：**这个职责应该被拆出来了。**

对于现有的代码仓，代码架构和规范都很乱，那么使用AI conding 新增代码时，怎么进行单元测试，怎么使用TDD的方式开发，同时又减少对现有业务功能的影响？

可以，但对于“遗留代码仓 + AI Coding”，我会把 TDD 理解成一种**增量安全开发策略**，而不是要求先把整个老系统重构成干净架构。

核心目标有三个：

> **新增代码可测试、行为可验证、对旧代码零侵入。**

最实用的做法是：**Characterization Test（行为基线） + Seam（隔离缝） + TDD + Contract/Integration Test + 小步提交。**


# 对于现有的代码仓，代码架构和规范都很乱，那么使用AI conding 新增代码时，怎么进行单元测试，怎么使用TDD的方式开发，同时又减少对现有业务功能的影响


可以。对于**现有代码仓很乱 + AI Coding 新增功能**，最怕的不是“单元测试写得少”，而是 AI 在没有边界的情况下修改旧逻辑，最后出现：

```text
新增需求
  ↓
AI 找到一个看起来能改的老方法
  ↓
顺手重构
  ↓
调用链变化
  ↓
老功能回归
  ↓
测试又不完整
  ↓
线上才发现

```

所以这类项目不能简单套“经典 TDD”。

我更推荐一套适合遗留系统的模式：

```text
                    新需求
                      │
                      ▼
              ① 明确变更边界
                      │
                      ▼
              ② 建立旧行为护栏
          Characterization Test
                      │
                      ▼
              ③ 找到隔离缝 Seam
                      │
                      ▼
              ④ 设计最小新增结构
                      │
                      ▼
                ⑤ TDD RED
                      │
                      ▼
                ⑥ TDD GREEN
                      │
                      ▼
               ⑦ 小范围 REFACTOR
                      │
          ┌───────────┴────────────┐
          ▼                        ▼
     Unit Test                Integration Test
          │                        │
          └───────────┬────────────┘
                      ▼
                 回归旧测试
                      │
                      ▼
                 小步提交代码

```

这套方法的核心不是“把旧代码整理干净”，而是：

> **不要碰旧代码，先在旧系统旁边建立一个新的、可测试的区域。**

下面详细说。

---

# 一、首先要改变一个观念：不要试图给整个老系统补齐单元测试

这是最重要的一点。

假设现有系统：

```text
com.xxx
├── controller
├── service
│   ├── OrderService.java        3000 行
│   ├── UserService.java         2000 行
│   └── RiskService.java         1500 行
├── manager
├── util
├── helper
├── common
├── mapper
└── client

```

里面可能存在：

```text
Service A
 ↓
Service B
 ↓
Manager C
 ↓
Utils
 ↓
DAO
 ↓
Redis
 ↓
HTTP

```

而且大量代码：

```java
@Service
public class OrderService {

    public Result createOrder(Request request) {

        // 500 行
        // 数据库
        // Redis
        // HTTP
        // MQ
        // 各种 if
        // static 工具
        // ThreadLocal
        // 时间
        // 全局变量
    }
}

```

这个时候千万不要想着：

> “AI，先帮我把 OrderService 重构一下，然后补单测。”

这是高风险操作。

正确策略是：

```text
旧系统
┌──────────────────────┐
│ Old OrderService     │
│ 老代码，不动         │
└──────────────────────┘

             ↓

新功能
┌──────────────────────┐
│ NewOrderPolicy       │
│ NewOrderCalculator   │
│ NewOrderValidator    │
│ NewOrderHandler      │
└──────────────────────┘

```

**新代码建立自己的边界。**

---

# 二、第一步：先定义“变更边界”

AI Coding 最重要的一件事情，其实不是让 AI 写代码，而是告诉 AI：

> **这次允许动什么，不允许动什么。**

例如你要新增：

> “订单创建时增加优惠券校验。”

不要直接给 AI：

```text
帮我增加优惠券校验

```

而应该把任务定义成：

```text
本次需求：
订单创建增加优惠券校验。

允许修改：
- OrderController
- 新增 CouponValidator
- 新增 CouponClient
- 新增 OrderCouponService
- OrderService 只允许增加 1 个调用点

禁止修改：
- OrderService 原有核心流程
- OrderRepository
- Coupon 旧模块
- 公共工具类
- 其他 Controller
- 数据库表结构

要求：
- 新增业务逻辑必须有单元测试
- 不允许修改现有接口语义
- 不允许大范围重构
- 原有流程保持不变

```

这其实是 AI Coding 的一个非常重要的实践：

> **给 AI 设置“修改预算”和“修改边界”。**

---

# 三、第二步：先建立 Characterization Test

这个概念对于遗留系统非常重要。

中文通常叫：

> **行为刻画测试 / 行为基线测试**

它和普通 TDD 不一样。

普通 TDD：

```text
需求
 ↓
测试
 ↓
代码

```

遗留系统：

```text
现有代码
 ↓
观察现有行为
 ↓
建立基线测试
 ↓
再修改

```

也就是说：

> 你不一定知道老代码“应该怎么样”，但你至少可以知道它“现在怎么样”。

---

## 举个例子

老系统：

```java
public Result createOrder(OrderRequest request) {

    if (request.getUserId() == null) {
        return Result.fail("USER_INVALID");
    }

    Order order = buildOrder(request);

    saveOrder(order);

    sendMessage(order);

    return Result.success(order.getId());
}

```

你不确定：

```text
userId = null

```

到底是不是要求返回：

```text
USER_INVALID

```

还是：

```text
PARAM_ERROR

```

还是直接抛异常。

不要猜。

先写：

```java
@Test
void should_keep_current_behavior_when_user_id_is_null() {

    Result result = oldOrderService.createOrder(request);

    assertThat(result.getCode())
        .isEqualTo("USER_INVALID");
}

```

这不是为了证明代码“设计正确”。

而是：

> **把现有行为冻结下来。**

以后 AI 改代码：

```text
Test PASS

```

说明至少没改变这个行为。

---

# 四、Characterization Test 不需要覆盖整个系统

这是另一个关键点。

你不需要：

```text
覆盖率 80%

```

一开始更重要的是覆盖：

### 本次需求影响到的路径

例如：

```text
createOrder()
   ↓
validate
   ↓
calculate
   ↓
save

```

那么优先建立：

```text
正常路径
异常路径
边界路径

```

例如：

```java
@Test
void create_order_success()

@Test
void create_order_when_user_not_found()

@Test
void create_order_when_amount_invalid()

@Test
void create_order_when_save_failed()

```

重点不是覆盖率，而是：

> **给 AI 修改这条链路建立“护栏”。**

---

# 五、第三步：找 Seam——这是遗留代码测试的核心

Michael Feathers 在《Working Effectively with Legacy Code》里非常强调这个概念：

> **Seam：可以不修改原代码行为，就替换部分行为的地方。**

对于 Java/Spring 项目，最常见的 Seam 是：

```text
Interface
构造函数
Spring Bean
方法参数
配置
Factory
Repository
Client
Event Publisher

```

例如老代码：

```java
public class OrderService {

    public Result createOrder(Request request) {

        User user = userService.findUser(request.getUserId());

        PaymentResult result =
                paymentClient.pay(
                    request.getUserId(),
                    request.getAmount()
                );

        ...
    }
}

```

虽然代码很乱，但其实已经有两个 Seam：

```text
UserService
PaymentClient

```

那么我们就可以：

```text
OrderService
   ↓
UserService      ← Mock
PaymentClient    ← Mock

```

这样就能测试。

---

# 六、如果老代码根本没有接口怎么办？

这个场景非常常见。

例如：

```java
public class OrderService {

    public Result createOrder(Request request) {

        UserService service = new UserService();

        return service.query(...);
    }
}

```

这时：

```text
new UserService()

```

就是问题。

不要一上来重构整个项目。

可以采取“向旁边移动”的策略。

例如新增：

```java
public interface UserGateway {

    User findUser(Long userId);
}

```

然后：

```java
@Component
public class UserGatewayImpl implements UserGateway {

    private final UserService userService;

    @Override
    public User findUser(Long userId) {
        return userService.queryUser(userId);
    }
}

```

然后新增代码：

```java
public class NewOrderFeature {

    private final UserGateway userGateway;

    public NewOrderFeature(UserGateway userGateway) {
        this.userGateway = userGateway;
    }
}

```

于是：

```text
旧系统

OrderService
    ↓
UserService

```

不动。

新系统：

```text
NewOrderFeature
    ↓
UserGateway
    ↓
UserService

```

单测：

```text
NewOrderFeature
    ↓
Mock UserGateway

```

这就是：

> **Adapter / Anti-Corruption Layer**

在遗留系统中特别有用。

---

# 七、第四步：开始真正的 TDD

到了这里，才开始经典 TDD。

假设需求是：

> 订单金额超过 1000 元，需要进行人工审核。

首先不要写实现。

先写：

```java
@Test
void amount_greater_than_1000_should_require_manual_review() {

    Order order = order(1001);

    ReviewDecision decision =
        service.evaluate(order);

    assertThat(decision)
        .isEqualTo(MANUAL_REVIEW);
}

```

运行：

```text
FAIL

```

然后只写最小实现：

```java
public ReviewDecision evaluate(Order order) {

    if (order.getAmount() > 1000) {
        return MANUAL_REVIEW;
    }

    return PASS;
}

```

再跑：

```text
PASS

```

然后继续：

```java
@Test
void amount_equal_to_1000_should_pass()

```

再：

```java
@Test
void amount_less_than_1000_should_pass()

```

这就是标准 TDD：

```text
RED
 ↓
GREEN
 ↓
REFACTOR

```

---

# 八、但是遗留系统的 TDD 和普通项目有一个重要区别

普通项目：

```text
先设计
 ↓
写接口
 ↓
TDD

```

遗留系统：

```text
先观察旧系统
 ↓
找到 Seam
 ↓
建立隔离
 ↓
TDD 新代码

```

所以可以理解为：

```text
Legacy TDD = Characterization + Seam + TDD

```

这套思路非常适合 AI。

---

# 九、第五步：AI 不要一次生成全部代码

这是我非常建议控制的地方。

错误方式：

```text
“帮我实现完整的优惠券系统，
包含 Controller、Service、DAO、Redis、单元测试。”

```

AI 很可能一次生成：

```text
20 个文件
3000 行代码

```

最后：

```text
测试
    ↓
全部一起修
    ↓
不知道哪里出了问题

```

正确方式是把任务切成极小的 TDD Loop。

---

## Step 1

让 AI：

```text
分析现有 OrderService。
只分析，不修改代码。

输出：
1. 调用链
2. 依赖
3. 外部资源
4. 本次需求影响路径
5. 建议的隔离点
6. 可能影响的旧功能

```

---

## Step 2

让 AI：

```text
不要改业务代码。

只为现有流程增加行为基线测试。
只覆盖本次需求可能影响的场景。

```

---

## Step 3

让 AI：

```text
设计新增功能的最小接口和类。
不要修改老业务逻辑。

```

例如：

```text
CouponValidator
CouponGateway
CouponResult

```

---

## Step 4

开始 TDD：

```text
先写失败测试。
不要实现业务代码。

```

---

## Step 5

再：

```text
只写使当前测试通过的最小实现。
不要额外重构。

```

---

## Step 6

然后：

```text
运行新增 Unit Test。
运行相关旧测试。
检查 diff。

```

---

# 十、这里有一个非常重要的东西：Golden Master

对于特别复杂、特别乱的老流程，我非常推荐。

例如：

```java
OldOrderService.createOrder(request)

```

你可以准备一批真实/脱敏输入：

```text
case-001.json
case-002.json
case-003.json
...
case-100.json

```

然后保存：

```text
Input
→ Output

```

例如：

```json
{
  "request": {...},
  "response": {
      "code": "SUCCESS",
      "amount": 100
  }
}

```

以后修改代码：

```text
新版
 ↓
输入
 ↓
输出

```

然后比较：

```text
旧结果 == 新结果

```

这就是一种：

> **Golden Master Test**

对于 AI Coding 非常有价值。

因为你不需要完全理解：

```text
3000 行老代码为什么这样写

```

你只需要确保：

```text
原来的 1000 个典型输入
→
仍然得到原来的结果

```

---

# 十一、对于依赖数据库的老代码怎么办？

例如：

```java
public Result createOrder(Request request) {

    User user = userMapper.selectById(request.getUserId());

    Order order = ...
    
    orderMapper.insert(order);

    return ...
}

```

不要一开始：

```text
Unit Test
 ↓
连接开发 MySQL

```

这样测试非常脆弱。

应该先把新业务拆出来。

例如：

```java
public class OrderPolicy {

    public OrderDecision evaluate(
            User user,
            OrderRequest request) {
        ...
    }
}

```

然后：

```text
OrderPolicyTest

```

完全不需要数据库。

数据库部分：

```text
OrderRepositoryIntegrationTest

```

再用：

```text
Testcontainers + MySQL

```

测试。

最终：

```text
Unit Test
80%左右

Integration Test
少量

```

---

# 十二、对于 Redis / MQ / HTTP 也是一样

建立不同层：

```text
业务规则
    ↓
Unit Test
    ↓
Fake / Mock

```

基础设施：

```text
Redis
MySQL
Kafka
HTTP
    ↓
Integration Test

```

API 合约：

```text
Contract Test

```

完整链路：

```text
E2E

```

不要把所有测试都搞成 Integration Test。

否则 AI 一改一个字段：

```text
500 个测试
×
Spring Boot 启动
×
MySQL
×
Redis

```

整个开发体验会非常糟糕。

---

# 十三、Private 方法怎么处理？

你上一条问的这个问题，在遗留系统里更加重要。

例如：

```java
public Result process(Request request) {

    validate(request);

    RiskResult risk = calculateRisk(request);

    save(risk);

    return buildResult(risk);
}

private RiskResult calculateRisk(Request request) {

    // 300 行
}

```

不要：

```java
ReflectionTestUtils.invokeMethod(
    service,
    "calculateRisk",
    request
);

```

而是：

```text
如果 calculateRisk 很复杂
        ↓
提取 RiskCalculator
        ↓
RiskCalculatorTest

```

但是注意：

> **只重构这一个点。**

不要借此机会：

```text
“顺便把 RiskService 全部重构了”

```

这就是 AI Coding 最大的风险之一。

---

# 十四、如何防止 AI 自己偷偷重构？

我建议给 AI 建立一个非常明确的规则。

例如在项目里放：

```text
AGENTS.md

```

或者：

```text
CLAUDE.md

```

或者你们自己的：

```text
AI-CODING.md

```

里面定义：

```text
# Legacy Code Rules

1. 不修改无关代码
2. 不进行大范围重构
3. 不改变已有 public API
4. 不修改数据库结构，除非需求明确要求
5. 新增业务逻辑必须可单元测试
6. 新代码优先依赖接口
7. 不为测试将 private 修改为 public
8. 优先通过 Adapter 隔离 legacy dependency
9. 每次修改保持最小 diff
10. 每个需求必须包含：
   - Unit Test
   - 相关 Integration Test
   - Regression Test
11. 如果发现现有代码存在架构问题：
   - 不要顺手修复
   - 记录为独立重构任务

```

这个文件对 AI Coding 非常有价值。

因为它相当于：

> **项目级 Coding Constitution**

---

# 十五、我建议再加入一个“改动影响分析”

AI 写代码之前，先让它回答：

```text
本次需求影响哪些代码？

```

例如：

```text
Requirement
    ↓
Controller
    ↓
OrderService
    ↓
CouponService
    ↓
CouponClient

```

然后标记：

```text
绿色：允许新增

黄色：允许增加调用

红色：禁止修改

```

例如：

```text
OrderController        🟡
OrderService           🟡
CouponValidator        🟢
CouponGateway          🟢
CouponClient           🟢
PaymentService         🔴
UserService            🔴

```

这样 AI 就不会因为：

```text
“这里顺便优化一下”

```

把 PaymentService 改了。

---

# 十六、Git 上建议采用极小提交

不要：

```text
feat: add coupon support

```

一个 commit 里面：

```text
100 files

```

我更建议：

```text
1. test: add order behavior characterization
2. test: add coupon validation cases
3. feat: add coupon validator
4. feat: add coupon gateway
5. feat: integrate coupon validation
6. test: add integration coverage

```

这样一旦出现：

```text
regression

```

非常容易定位：

```text
git bisect

```

AI Coding 特别适合这种方式。

---

# 十七、最终形成一个“安全开发回路”

整个开发过程可以固定成：

```text
┌──────────────────────────────┐
│          用户需求             │
└───────────────┬──────────────┘
                ↓
       Impact Analysis
                ↓
       找到旧代码影响边界
                ↓
      Characterization Test
                ↓
      建立旧行为护栏
                ↓
        设计新的 Seam
                ↓
      新代码 / 新接口
                ↓
            TDD RED
                ↓
           最小实现
                ↓
           TDD GREEN
                ↓
        小范围 Refactor
                ↓
       Unit + Integration
                ↓
       Legacy Regression
                ↓
          Diff Review
                ↓
          小步 Commit

```

---

# 十八、可以把测试体系理解成“四道防线”

对于你的场景，我会这样设计：

| 层级 | 目标 | 是否真实依赖 |
| --- | --- | --- |
| Unit Test | 新业务逻辑正确 | 否 |
| Characterization Test | 老业务行为不变 | 通常是 |
| Integration Test | DB/Redis/HTTP 等真实集成 | 是 |
| E2E / Regression | 核心业务链路不挂 | 是 |

其中最容易被忽略的是第二层：

### Characterization Test

它不是为了证明：

> “老代码是正确的。”

而是为了保证：

> **“你新增代码以后，老代码原来干什么，还是干什么。”**

这对遗留系统尤其重要。

---

# 十九、一个完整 Java/Spring Boot 示例

假设现有：

```java
@Service
public class OrderService {

    public Result createOrder(OrderRequest request) {

        User user = userService.find(request.getUserId());

        if (user == null) {
            return Result.fail("USER_NOT_FOUND");
        }

        Order order = buildOrder(request, user);

        orderMapper.insert(order);

        return Result.success(order.getId());
    }
}

```

现在新增需求：

> VIP 用户订单金额超过 5000，需要风控审核。

---

## 第一步：先冻结原有行为

```java
@Test
void normal_order_should_keep_original_behavior() {

    Result result = oldFlow(...);

    assertThat(result.getCode())
        .isEqualTo("SUCCESS");
}

```

---

## 第二步：设计新对象

```java
public interface RiskEvaluator {

    RiskDecision evaluate(User user, Order order);
}

```

实现：

```java
public class VipRiskEvaluator
        implements RiskEvaluator {

    @Override
    public RiskDecision evaluate(
            User user,
            Order order) {

        if ("VIP".equals(user.getLevel())
                && order.getAmount()
                       .compareTo(BigDecimal.valueOf(5000)) > 0) {

            return RiskDecision.REVIEW;
        }

        return RiskDecision.PASS;
    }
}

```

---

## 第三步：TDD

先：

```java
@Test
void vip_order_over_5000_should_review()

```

然后：

```java
@Test
void vip_order_equal_5000_should_pass()

```

再：

```java
@Test
void normal_user_over_5000_should_pass()

```

---

## 第四步：Integration

验证：

```text
OrderService
 ↓
RiskEvaluator
 ↓
DB

```

是否正确接起来。

---

## 第五步：回归

确保：

```text
旧用户
普通订单
原有支付流程
原有库存流程

```

都没变。

---

# 二十、最关键的其实是“新增代码向外，旧代码不动”

可以把这个思想抽象成：

```text
        Legacy System
┌─────────────────────────┐
│                         │
│       老代码             │
│       尽量冻结           │
│                         │
└─────────────┬───────────┘
              │
          Adapter / Seam
              │
              ▼
┌─────────────────────────┐
│      New Code           │
│                         │
│ Domain                  │
│ Policy                  │
│ Validator               │
│ Gateway                 │
│ Application Service     │
│                         │
│       可测试             │
└─────────────────────────┘

```

这叫一种很实用的：

> **Strangler / 萌芽式重构思路**

不是：

```text
把旧系统重写

```

而是：

```text
新功能逐渐长在旧系统外面
 ↓
新代码越来越多
 ↓
旧代码越来越少被直接依赖

```

---

# 二十一、我最推荐你给 AI 建立的一套标准 Prompt

实际 Coding 的时候，可以固定这么做：

```text
你正在一个遗留 Java/Spring Boot 项目中开发新需求。

请严格遵守：

【目标】
只实现本次需求，不进行无关重构。

【第一阶段：分析】
不要修改代码。
先分析：
1. 需求影响的调用链
2. 涉及的老代码
3. 外部依赖
4. 可测试的隔离点 Seam
5. 可能的回归风险
6. 建议增加哪些测试

【第二阶段：行为基线】
如果涉及老代码，请先增加 Characterization Test，
用于冻结当前行为。
不要改变旧业务代码。

【第三阶段：设计】
新增业务优先采用：
- interface
- adapter
- gateway
- domain/service
以隔离 legacy dependency。

禁止：
- 为了测试修改 private 为 public
- 大范围重构
- 修改无关类
- 修改 public API
- 修改数据库结构

【第四阶段：TDD】
严格执行：
RED → GREEN → REFACTOR

先增加失败测试，
再写最小实现，
然后再重构。

【第五阶段：验证】
至少运行：
1. 新增 Unit Test
2. 相关 Integration Test
3. 本次影响路径的 Regression Test

【第六阶段：输出】
最终告诉我：
- 修改了哪些文件
- 为什么修改
- 新增哪些测试
- 哪些旧代码没有修改
- 可能的回归风险

```

这比一句：

```text
“帮我加个功能并写单测”

```

效果通常好很多。

---

# 二十二、如果把整个方法浓缩成一句话

对于**混乱遗留代码仓 + AI Coding**：

> **不要先重构旧系统来适应 TDD，而是先用 Characterization Test 给旧系统加护栏，再通过 Seam 把新逻辑隔离出来，在新代码内部执行真正的 TDD，最后用 Integration/Regression Test 防止新旧系统连接处出问题。**

可以记成：

```text
Legacy Code
    ↓
Characterization Test
    ↓
Safety Fence
    ↓
Seam / Adapter
    ↓
New Code
    ↓
TDD
    ↓
Unit Test
    ↓
Integration Test
    ↓
Regression

```

这实际上比“所有代码都必须单元测试”更加现实，也更适合 AI Coding。

如果把它进一步落到**你的 Java/Spring Boot 项目**上，下一步最有价值的是把这套方法变成一套实际可执行的 **“AI Coding 遗留项目开发规范”**：包括 `AGENTS.md`、目录规范、测试目录规范、Git Commit 规范，以及一套可以直接复制给 Claude Code / Cursor / Codex 的 TDD Prompt 模板。

