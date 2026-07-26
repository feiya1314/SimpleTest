# Matt Pocock Claude Code 工作流分析报告

> 来源：https://github.com/mattpocock/skills — Matt Pocock 的开源 Claude Code 技能集
> 分析日期：2026-07-26

---

## 一、核心理念：技能即「确定性提取器」

Matt 的核心观点：**AI 是随机的，但工程需要确定性**。Skills 的作用就是从随机系统中"拧出确定性"——不是每次产生相同输出，而是每次**遵循相同的过程**。

> *"Predictability is the root virtue, and every design choice is judged against it."*

这解释了整个系统的设计基调——**小而可组合**，**每个 skill 只做一件事**，**用户调用 vs 模型自动调用有严格区分**。

---

## 二、完整编码工作流

```
[模糊想法]
    │
    ▼
Phase 1: 对齐（Grilling）
  /grill-me 或 /grill-with-docs
  → 反复追问直到理解一致
  → /domain-modeling 同步更新 CONTEXT.md
  → 必要时写 ADR（架构决策记录）
    │
    ▼
Phase 2: 写 Spec（规格说明书）
  /to-spec
  → Problem Statement → Solution
  → User Stories（大量）
  → Implementation Decisions
  → Testing Decisions + Seams
  → Out of Scope
  发布到 Issue Tracker
    │
    ▼
Phase 3: 拆 Tickets
  /to-tickets
  → 垂直切片（每个 ticket 穿全栈）
  → 声明依赖关系（blocking edges）
  → 用户确认粒度
  发布到 Issue Tracker
    │
    ▼
Phase 4: 实现
  /implement
  → /tdd 红-绿-重构循环
  → 在预先商定的 seams 处测试
  → 定期跑类型检查 + 单个测试文件
  → 最终跑完整测试套件
  → /code-review 两轴审查
  → 提交到当前分支
```

---

## 三、关键技巧详解

### 🔥 技巧 1：Grilling — 一切从对齐开始

**这是 Matt 最核心的技巧**，也是他整个工作流的起点。

- `/grill-me`（非编码场景）：反复追问直到达成共识
- `/grill-with-docs`（编码场景）：在追问的同时建立领域语言

**工作原理**：
- 每次只问一个问题（"一次问多个问题会让人困惑"）
- 能查代码/文件的不问用户（"事实"可以查，"决策"才问）
- 问完后给出**推荐答案**让用户确认
- 走遍决策树的每个分支

**为什么**：Matt 引用《程序员修炼之道》——"没有人一开始就知道自己想要什么"。Grilling 就是为了消除你和 AI 之间的"沟通鸿沟"。

> **使用建议**：每次开始新功能前，先让 AI 追问你一轮。不是直接说"帮我实现 X"，而是说"我要实现 X，先 grill 我"。

---

### 📖 技巧 2：Domain Modeling — 建立共享语言

Matt 说这可能**是整个项目中最酷的技巧**。

**核心文件**：
- `CONTEXT.md` — 项目术语表（Glossary），**只放术语定义，不放实现细节**
- `docs/adr/NNNN-name.md` — 架构决策记录（Architecture Decision Records）

**工作原理**：
1. 当你用模糊术语时，AI 立即指出来："你说的 'account' 是指 Customer 还是 User？这是不同的东西。"
2. 术语确定后，**立即**更新 `CONTEXT.md`
3. 只有满足**三个条件**才写 ADR：
   - 很难撤销
   - 没有上下文会让人困惑
   - 是真正的权衡结果

**收益**：
- AI 用更少的词表达更多含义
- 变量、函数、文件名一致
- AI 花更少 token 思考，因为语言更精确

> **使用建议**：在你的项目中创建一个 `CONTEXT.md`，定义关键术语。每次发现模糊用语就补充。

---

### 🎯 技巧 3：To-Spec → To-Tickets — 规格驱动

**Spec 模板包含**：
- Problem Statement（用户视角的问题）
- Solution（用户视角的解决方案）
- User Stories（大量的，格式：As a <角色>, I want <功能>, so that <收益>）
- Implementation Decisions（模块、接口、架构决策、API 契约 — **不要具体文件路径**）
- Testing Decisions（测试的 seams，即在哪里测试）
- Out of Scope（明确什么不做）

**Ticket 拆分原则**：
- **垂直切片**：每个 ticket 贯穿全栈（Schema → API → UI → 测试），不是水平切一层
- 每个 ticket 完成后**可以独立验证**
- 每个 ticket 大小适合**一次全新的对话窗口**
- 声明依赖关系（Blocked by）

> **使用建议**：不要直接说"帮我实现"，先让 AI 写 Spec，再拆 Tickets，确认后再开始写代码。

---

### 🗺️ 技巧 4：Wayfinder — 大型工作的导航系统

当工作量大到**一次对话装不下**时使用。

**核心概念**：
- **Map**（地图）：一个 Issue，标记 `wayfinder:map`，包含 Destination、Decisions so far、Fog of war、Out of scope
- **Decision Ticket**（决策票）：子 Issue，每个解决一个问题，大小约 100K token
- **Frontier**（前沿）：当前可以开始工作的 tickets（所有依赖都已关闭）
- **Fog of war**（战争迷雾）：知道即将到来但还不能精确定义的问题

**Ticket 类型**：
1. **Research** (AFK) — 阅读文档、调查 API，AI 自行完成
2. **Prototype** (HITL) — 需要和用户交互的原型
3. **Grilling** (HITL) — 通过对话解决的（默认类型）
4. **Task** (HITL/AFK) — 在决策之前必须完成的体力活

**规则**：一次会话只解决一个 ticket（research 除外）。

> **使用建议**：当面对一个大的、模糊的任务时，不要直接开干。先让 AI 帮你画地图、拆 tickets，然后一个一个解决。

---

### 🔁 技巧 5：TDD — 红-绿-重构循环

**好测试的定义**：通过公共接口验证行为，而不是实现细节。测试读起来像规格说明书。

**关键规则**：
- **预先商定 Seams**：在写任何测试前，先和用户确认在哪里测试
- **先红后绿**：先写失败的测试，再写足够让它通过的代码
- **一次一个切片**：一个 seam，一个测试，一个最小实现
- **反模式**：
  - 和实现耦合（测试内部方法）
  - 同义反复（断言自己算出来的值）
  - 水平切片（先写完所有测试再写实现）

> **使用建议**：让 AI 先写测试再写代码。在复杂逻辑前，先让 AI 确认测试点（seams）。

---

### 👁️ 技巧 6：Code Review — 两轴审查

**两个维度并行审查**：
1. **Standards**（标准轴）：代码是否符合项目规范 + Fowler 代码坏味道检查
2. **Spec**（规格轴）：代码是否忠实实现了需求

**为什么两个轴**：一个可以通过另一个的失败来掩盖。
- 代码写得很规范但实现错了 → Standards 通过，Spec 失败
- 实现了需求但破坏了规范 → Spec 通过，Standards 失败

**Fowler 坏味道基准线**：Mysterious Name, Duplicated Code, Feature Envy, Data Clumps, Primitive Obsession, Repeated Switches, Shotgun Surgery, Divergent Change, Speculative Generality, Message Chains, Middle Man, Refused Bequest

> **使用建议**：每次提交前让 AI 做两轴审查。特别是"Spec"轴——检查是否真的实现了需求。

---

### 🧠 技巧 7：Codebase Design — 深度模块

Matt 深受 John Ousterhout 的《软件设计哲学》影响。

**核心概念**：
- **深度模块**（Deep Module） = 小接口 + 大量实现
- **浅模块**（Shallow Module） = 大接口 + 少实现（应避免）
- **Seam** = 测试和替换的边界点
- **Adapter** = 在 Seam 处满足接口的具体实现

**设计原则**：
- 减少方法数量
- 简化参数
- 把更多复杂度藏在接口后面
- "删除测试"：删掉这个模块，复杂度会消失还是分散到 N 个调用方？
- "一个 adapter 是假设的 seam，两个 adapter 才是真实的 seam"

> **使用建议**：让 AI 在设计接口时使用"深度模块"语言。问："这个模块的 depth 如何？接口能不能更小？"

---

### 📤 技巧 8：Handoff — 跨会话交接

当一次对话装不下时，使用 `/handoff` 生成交接文档。

**关键规则**：
- 保存到系统临时目录（不污染项目）
- 包含"suggested skills"章节
- 不要重复已经存在于其他 artifact（spec、plan、ADR、commit、diff）中的内容——引用路径/URL 即可
- 隐去敏感信息

> **使用建议**：当对话太长或要切换任务时，用 `/handoff` 生成交接文档，下一个会话可以继续。

---

## 四、技能架构的核心设计原则

### 1. 用户调用 vs 模型自动调用

| | 用户调用 | 模型自动调用 |
|---|---|---|
| 谁触发 | 只有用户输入 `/skill-name` | 模型或用户都可以 |
| 上下文成本 | 零（不占用每次对话上下文） | 每次对话都占用描述空间 |
| 认知成本 | 你需要记住它存在 | 零 |
| 例子 | `/grill-me`, `/to-spec`, `/wayfinder` | `tdd`, `research`, `domain-modeling` |

**关键洞察**：当用户调用的技能多到记不住时，需要一个"路由技能"（`/ask-matt`）来帮你导航。

### 2. 技能的小型化和可组合性

- 每个 skill 的 `SKILL.md` 通常只有几行到几十行
- 复杂逻辑拆到引用文件中（如 `tests.md`, `mocking.md`, `CONTEXT-FORMAT.md`）
- 一个 skill 可以调用另一个 skill（如 `grill-with-docs` 调用 `domain-modeling`）

### 3. 信息层次（Progressive Disclosure）

```
最顶层：技能步骤（in-skill step）
  ↓
中间层：技能内部引用（in-skill reference）
  ↓
最深层：外部文件（context pointer）
```

读 skill 时从顶层开始，需要时再深入。

---

## 五、使用行动计划

### 第一阶段：立即开始使用

1. **Grilling 习惯**：每次开始新功能前，说"先 grill 我"
2. **创建 CONTEXT.md**：定义项目的关键术语
3. **Spec 驱动**：让 AI 先写 Spec 再写代码

### 第二阶段：引入质量门禁

4. **TDD 循环**：让 AI 先写测试
5. **两轴 Code Review**：提交前做 Standards + Spec 审查
6. **Seam 确认**：写代码前确认在哪里测试

### 第三阶段：处理大型工作

7. **Wayfinder**：面对模糊的大任务，先画地图再行动
8. **To-Tickets**：把 Spec 拆成可执行的 tickets
9. **Handoff**：跨会话交接

### 第四阶段：架构演进

10. **深度模块设计**：用 deep module 语言设计接口
11. **Domain Modeling**：持续精炼术语和记录架构决策
12. **Improve Codebase Architecture**：定期运行，防止代码变成"泥球"

---

## 六、Matt 的哲学金句

> *"Developing real applications is hard. Approaches like GSD, BMAD, and Spec-Kit try to help by owning the process. But while doing so, they take away your control."*
>
> — 这些技能小而可控，不给 AI 太多自主权

> *"The rate of feedback is your speed limit."*
>
> — 测试和类型检查是反馈循环，决定了你能多快

> *"Invest in the design of the system every day."*
>
> — 每天投资系统设计，防止 AI 加速软件熵

> *"The best modules are deep. They allow a lot of functionality to be accessed through a simple interface."*
>
> — 深度模块是 AI 时代更重要的设计原则

---

## 七、实战问答

### Q1：我有一个现有项目，有个需求要实现，具体怎么做？

以下是可直接执行的 7 步流程：

#### 第 1 步：让 AI 理解项目上下文

```
我有个项目在 /path/to/project，先读一下项目结构和关键文件
```

如果项目还没有 `CONTEXT.md`，让 AI 帮你建一个：

```
读一下这个项目的代码，帮我提炼关键术语，建一个 CONTEXT.md
```

#### 第 2 步：Grill 我（最关键的一步）

**不要直接说"帮我实现 X"**，而是说：

```
我有一个需求：[简要描述]
先 grill 我，直到你完全理解我要做什么
```

AI 会一次问一个问题，给出推荐答案让你确认，走遍决策树的每个分支。

**为什么这一步最重要**：因为"没有人一开始就知道自己想要什么"。Grilling 消除你和 AI 之间的理解偏差。

#### 第 3 步：写 Spec（规格说明书）

```
现在基于我们讨论的内容，帮我写一个 Spec
```

AI 会自动生成包含 Problem Statement、Solution、User Stories、Implementation Decisions、Testing Decisions（seams）、Out of Scope 的完整 Spec。**你确认没问题再继续。**

#### 第 4 步：拆 Tickets

```
把 Spec 拆成可执行的 tickets，垂直切片，声明依赖关系
```

每个 ticket 贯穿全栈，并标明哪些可以并行、哪些有依赖关系。**你确认粒度合适再继续。**

#### 第 5 步：逐个实现（TDD 循环）

```
从第一个 ticket 开始，用 TDD 方式实现
```

AI 会先写测试（红），再写刚好能通过的代码（绿），定期跑类型检查和单测。

#### 第 6 步：Code Review

```
对这次变更做两轴 code review：
1. Standards — 代码规范 + 坏味道
2. Spec — 是否忠实实现了需求
```

#### 第 7 步：提交

确认没问题后提交。

**一句话总结转变**：

| 你现在的习惯 | 你应该做的 |
|---|---|
| "帮我实现 X" | "先 grill 我，再写 Spec，再拆 tickets，再 TDD 实现，再 code review" |

关键转变：从"直接让 AI 写代码"变成"先让 AI 理解你，你再确认，再让 AI 写"。

---

### Q2：Spec 怎么维护？每个需求都要新增 Spec 吗？

#### Spec 的定位：一次性沟通工具，不是长期文档

Matt 把 Spec 发布到 Issue Tracker，打上 `ready-for-agent` 标签。生命周期：

```
写 Spec → 确认 → 拆 Tickets → 实现 → 关闭
                                        ↑
                                  Spec 的使命结束了
```

Spec **不是** Wiki、设计文档或 README。它是"在开始写代码前，确保你和 AI 理解一致"的中间产物。代码写完，Spec 的历史使命就完成了。

#### 什么情况需要写 Spec？

| 场景 | 要不要 Spec | 原因 |
|---|---|---|
| **新功能**（1天以上工作量） | ✅ 要 | 涉及多个模块、多个决策点 |
| **小功能**（几小时） | ✅ 建议写 | 简单写几句，确保对齐 |
| **Bug 修复** | ❌ 不需要 | 直接走诊断流程 |
| **重构** | ❌ 不需要 | 直接用架构改进流程 |
| **微调**（改个文案/颜色） | ❌ 不需要 | 直接改 |

**简单判断标准**：如果"几句话说不清楚"就需要 Spec；一句话就说清楚了就不需要。

#### 多个需求时，Spec 怎么组织？

**每个需求一个独立的 Spec**，不要堆在一起：

```
需求 A（用户登录）→ Spec A → Tickets A1, A2, A3 → 实现
需求 B（支付功能）→ Spec B → Tickets B1, B2 → 实现
```

**为什么**：每个独立评审排期，一个变更不影响其他，关闭后就是历史记录不会腐烂。

#### Spec 变更了怎么办？

- **还没开始实现** → 更新 Spec：`之前讨论的 Spec 有变化，更新一下`
- **已经实现了一半** → 关掉旧 Spec，重新来

不要试图让一个 Spec 追踪所有变更——它只是对齐工具，不是需求追踪器。

#### 真正需要长期维护的，是这三样

| 文档 | 维护频率 | 内容 |
|---|---|---|
| **CONTEXT.md** | 每次发现模糊术语时 | 项目术语表（Glossary） |
| **docs/adr/** | 做出重要架构决策时 | 架构决策记录（ADR） |
| **Issue Tracker** | 持续 | 已关闭的 Spec 和 Tickets（历史记录） |

**Spec 是消耗品**，用完就关。真正需要用心维护的是 **CONTEXT.md** 和 **ADR**，它们才是跨会话、跨需求的长期资产。
