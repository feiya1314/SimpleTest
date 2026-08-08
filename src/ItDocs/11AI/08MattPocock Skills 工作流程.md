# Matt Pocock Skills 工作流程

## 核心结论

Matt Pocock 的 Skills 仓库是一套**可组合的工程实践集合**——把几十年的软件工程方法论（DDD、TDD、ADR、红绿重构、深模块设计）蒸馏成 AI Agent 可消费的结构化指令。它的核心洞察是：**AI Agent 的质量上限不是模型能力，而是你给它的工程流程框架。**

这套体系与 GSD、BMAD、Spec-Kit 这类"全流程托管"方案的根本区别在于：**它不接管你的开发流程，而是让你在需要的时候调用特定的 skill**。每个 skill 小而边界清晰，保持你对流程的控制权。

> 注意：本文基于**当前仓库版本 v1.2.3** 整理。如果你看到的参考笔记里提到 `/diagnose`、`/to-issues`、`/to-prd`、`/caveman`、`/zoom-out`——这些是**旧版**的 skill，当前仓库已改名或删除，对应关系见下表。

**新旧版对应关系（重要）**：

- `/diagnose` → `/diagnosing-bugs`（改名）
- `/to-issues` → `/to-tickets`（改名）
- `/to-prd` → `/to-spec`（改名，且不再访谈用户）
- `/caveman`、`/zoom-out` → 已删除（理念融入 `/writing-for-agents`、`/wayfinder`）
- 新增：`/wayfinder`（超大任务规划）、`/implement`（实现编排）、`/ask-matt`（路由器）、`/wizard`、`/prototype`、`/research`、`/to-questionnaire`

## 仓库概览

### 定位

仓库 README 开篇一句话点题：**"My agent skills that I use every day to do real engineering - not vibe coding."**（我每天用来做真实工程、而非"氛围编程"的 agent skills）。开发真实应用很难，GSD、BMAD、Spec-Kit 试图通过"拥有流程"来帮忙，但同时也夺走了你的控制权，让流程中的 bug 难以修复。而 Matt 的 skills：

- **小而可组合（small, easy to adapt, and composable）**
- **与模型无关（work with any model）**
- **基于数十年的工程经验**

### 目录结构（bucket 划分）

```
skills/
├── .claude-plugin/plugin.json    # Claude Code 插件入口（只声明 promoted 集）
├── .claude-plugin/marketplace.json
├── CONTEXT.md                    # 本仓库自己的域语言定义（DDD 通用语言）
├── CLAUDE.md                     # 给 agent 的仓库指令（治理规则）
├── docs/                         # 面向人类的 skill 文档页（aihero.dev/skills-*）
│   ├── engineering/
│   └── productivity/
├── scripts/                      # link-skills.sh、list-skills.sh、sync-plugin-version.mjs
├── .agents/                      # 维护者指令 + 2 个 ADR
└── skills/
    ├── engineering/              # 18 个，promoted
    ├── productivity/             # 7 个，promoted
    ├── misc/                     # 4 个，不推广
    ├── in-progress/              # 6 个，beta 公开求反馈
    └── deprecated/               # 当前为空
```

**分桶治理铁律**（来自 CLAUDE.md）：`engineering/` 和 `productivity/` 是 **正式发布（promoted）** 的桶，桶里的 skill 必须同时出现在顶层 README 引用和 `plugin.json` 的 `skills` 数组里；`misc/`、`in-progress/`、`deprecated/` 绝不能出现。通过**文件位置**而非开关来控制可见性，是典型的"约定优于配置"（convention-over-configuration）。

### 25 个 promoted skills 全清单

**Engineering（18 个，日常代码工作）**：

- User-invoked（9 个，只能人触发）：`ask-matt`、`grill-with-docs`、`triage`、`improve-codebase-architecture`、`setup-matt-pocock-skills`、`to-spec`、`to-tickets`、`implement`、`wayfinder`
- Model-invoked（9 个，模型可自动抓取）：`prototype`、`diagnosing-bugs`、`research`、`tdd`、`domain-modeling`、`codebase-design`、`code-review`、`resolving-merge-conflicts`、`wizard`

**Productivity（7 个，非代码工作流）**：

- User-invoked（5 个）：`grill-me`、`handoff`、`teach`、`to-questionnaire`、`wait-what`
- Model-invoked（2 个）：`grilling`、`writing-for-agents`

## 核心哲学：4 大失败模式及解法

Matt 构建这套 skills 的动机，是修复他在 Claude Code、Codex 等编码 agent 上反复看到的 4 大失败模式：

### 失败模式 1：Agent 没做你想要的（沟通对齐缺失）

> "No-one knows exactly what they want" — *The Pragmatic Programmer*

**问题**：软件开发最常见的失败模式是对齐失败。你以为 agent 理解了你，看到成果才发现根本没懂。AI 时代同样如此——你和 agent 之间存在 communication gap。

**解法**：**拷问式访谈（grilling session）**——让 agent 在开始前详细追问你要构建什么。

- `/grill-me`（非代码场景）
- `/grill-with-docs`（代码场景，额外构建共享语言和 ADR）

这是整个仓库**最受欢迎**的 skills。Matt 建议：**"Use them every time you want to make a change"**（每次想改动都用）。

### 失败模式 2：Agent 输出太啰嗦（缺乏共享语言）

> "With a ubiquitous language, conversations among developers and expressions of the code are all derived from the same domain model." — Eric Evans, DDD

**问题**：项目初期，开发者和领域专家通常说着不同的语言。agent 被丢进项目后要现学行话，于是**用 20 个词表达 1 个词就够的事**。

**解法**：**共享语言（shared language）**——一份帮助 agent 解码项目行话的文档（`CONTEXT.md`）。

真实示例（来自 course-video-manager 仓库）：

- 改前："There's a problem when a lesson inside a section of a course is made 'real' (i.e. given a spot in the file system)"
- 改后："There's a problem with the materialization cascade"（物化级联）

**这个简洁性收益会跨会话复利**。它内建在 `/grill-with-docs` 里。Matt 说："It might be the single coolest technique in this repo."（这可能是本仓库最酷的技术。）

共享语言的额外收益：

- **变量、函数、文件命名一致**，都使用共享语言
- 因此**代码库（codebase）对 agent 更容易导航**
- agent 的**思考 token 更少**，因为它有更简洁的语言可用

### 失败模式 3：代码跑不通（缺少快速反馈回路）

> "The rate of feedback is your speed limit." — *The Pragmatic Programmer*

**问题**：即使对齐了，agent 仍可能产出垃圾——因为**没有反馈回路**，agent 在盲飞（flying blind）。

**解法**：建立常规反馈回路：静态类型、浏览器访问、自动化测试。

- `/tdd`：红绿重构循环。agent 先写失败测试再修复，给 agent 一致的反馈水平，产出更好的代码。
- `/diagnosing-bugs`：把最佳调试实践封装成**按阶段门控的纪律性循环**。

### 失败模式 4：我们建了一座泥球（Ball of Mud）

> "Invest in the design of the system every day." — Kent Beck
> "The best modules are deep." — John Ousterhout

**问题**：agent 能极大加速编码，**同时也以前所未有的速度加速软件熵**——代码库以空前速度变复杂、难以修改。

**解法**：**在乎代码设计**，内建在每一层：

- `/to-spec` 在创建 spec 前拷问你要碰哪些模块
- `/improve-codebase-architecture` 扫描代码库，找"加深机会"（deepening opportunities，把浅模块改造成深模块的候选）并交出候选清单。Matt 建议**每几天跑一次**。"It is a survey, not a rescue"（它是普查，不是救援）——旧代码库上能找到真实候选，但不会替你解开泥球。

### 总结

软件工程基础原则比以往更重要。这些 skills 是 Matt 把基础原则浓缩成可重复实践的最佳尝试。

## 安装与配置

### 两条互斥的安装路径

> "Pick one — installing both leaves you with every skill twice."（二选一，都装会每个 skill 重复两份）

**路径 A：Claude Code 官方插件（订阅制、只读、自动更新）**

```bash
claude plugins install mattpocock-skills
# 或会话内：
/plugin install mattpocock-skills
```

这是官方 marketplace 收录的插件。**"you subscribe rather than fork"**——订阅而非分叉，Matt 发布更新时自动到达。

**路径 B：skills.sh 通用安装器（可编辑、可 hack）**

```bash
npx skills@latest add mattpocock/skills
```

**"copies editable skill files into your project, so you can hack on them and make them your own"**——把可编辑的 skill 文件复制进项目，你可以改造成自己的。适合 Codex、其他 agent 或想折腾的人。**安装时务必勾选** `setup-matt-pocock-skills`。

### 一次性配置：`/setup-matt-pocock-skills`

在 agent 中每个仓库运行一次，它做三件事：

1. **问题跟踪器（Issue tracker，issue 放哪）**：GitHub（`gh` CLI）/ GitLab（`glab` CLI）/ 本地 markdown（`.scratch/` 目录）/ 其他自定义
2. **Triage 标签**（`/triage` 用的五个规范角色的实际标签字符串）
3. **Domain docs 布局**（`CONTEXT.md` 和 ADR 放哪，单上下文 vs 多上下文）

它是"prompt-driven skill, not a deterministic script"——先探索、呈现发现、与你确认、再写入。产出写入 `CLAUDE.md`/`AGENTS.md` 的 `## Agent skills` 块 + `docs/agents/*.md`。

### Hard vs Soft 依赖分层（架构决策 ADR-0001）

不是所有 skill 都同样依赖配置：

- **Hard dependency**（`to-tickets`、`to-spec`、`triage`）：必须有配置才能正确运行。**没有配置输出就是错的**（不只是"效果差一点"），所以 SKILL.md 里带显式指针 "run `/setup-matt-pocock-skills` if not"。
- **Soft dependency**（`tdd`、`diagnosing-bugs`、`improve-codebase-architecture`）：有配置输出更精准，没配置也能正常跑（只是没那么准）。只在文字里泛泛提"项目领域词表"和"相关区域的 ADR"，不占额外的 token。

这个区分防止把"请先跑 setup"这句话机械地复制到所有 skill 里。

## 架构机制

### 双触发机制：User-invoked vs Model-invoked

每个 skill 分两类触发方式，这是整个架构的关键轴：

- **User-invoked（用户触发）**：只有你输入名字才能触发（`disable-model-invocation: true`）。它们的职责是**编排（orchestrate）**——比如 `ask-matt`、`grill-with-docs`、`implement`、`wayfinder`。description 是**面向人**的，一行摘要，不带触发词列表。
- **Model-invoked（模型触发）**：默认状态，模型可自动抓取。它们承载**可复用的纪律**——比如 `tdd`、`diagnosing-bugs`、`grilling`。description 是**面向模型**的，保留富触发措辞（"Use when the user wants…, mentions…, asks for…"）让自动触发生效。

**关键规则**："A user-invoked skill may invoke model-invoked skills, but it can never reach another user-invoked skill."（用户触发的 skill 可以调用模型触发的 skill，但绝不能调到另一个用户触发的 skill。）

判断标准："could the model usefully reach for this autonomously?"（模型自主抓取它是否有用？）

### CONTEXT.md：通用语言（ubiquitous language）

`CONTEXT.md` 是每个仓库维护的领域术语表，结构规范：

```
## Language

**Issue tracker**:
The tool that hosts a repo's issues — GitHub Issues, Linear, a local `.scratch/` markdown convention.
_Avoid_: backlog manager, backlog backend, issue host

**Issue**:
A single tracked unit of work inside an **Issue tracker** — a bug, task, spec, or slice.
_Avoid_: ticket（除非引用外部系统）

## Relationships

- An **Issue tracker** holds many **Issues**
- An **Issue** carries one **Triage role** at a time

## Flagged ambiguities

- "backlog" was previously used to mean both the tool and the body of work — resolved: the tool is the **Issue tracker**.
```

- **词条格式**：粗体术语 + 定义 + `_Avoid_:` 禁止词列表
- **Relationships 区**：布尔式关系句，帮 agent 建立正确的对象关系
- **Flagged ambiguities 区**：记录历史混用过的词及裁决

**纪律**：`CONTEXT.md` 必须是**纯术语表**——"It is a glossary and nothing else." 绝不含实现细节，不当作 spec 或 scratch pad。

### ADR：三条件门槛

ADR（架构决策记录）是给 agent 看的决策记忆。但**只有三个条件同时满足才写**：

1. **Hard to reverse** — 改变这个决定的成本有意义
2. **Surprising without context** — 未来读者会困惑"为什么这么做"
3. **The result of a real trade-off** — 存在真实备选方案

任何一条缺失就跳过。这防止 ADR 目录变成无意义的文档墓地。ADR 本身可以极简——"# {Short title}" + "1-3 sentences: what's the context, what did we decide, and why. That's it."

### 词汇层（Vocabulary underneath）

整个体系之下跑着**两个可被模型触发的词汇层**，各自是某类词汇的**唯一权威定义**（单一事实源）：

- `/domain-modeling` — 领域语言（DDD）：挑战模糊术语、消解一个词身兼数职（"'account' doing three jobs"）、把难逆转的决定写成 ADR
- `/codebase-design` — 结构语言（深模块）：module（模块）、interface（接口）、depth（深度）、seam（测试入口）、adapter（适配器）、leverage（杠杆效应）、locality（局部性）等词的精确定义

这两个词汇层不是流程，是**语言**——"Consistent language is the whole point."（用词一致才是关键。）

### docs/skill-pages 体系

每个 promoted skill 有一个**面向人类读者**的文档页（`docs/<bucket>/<skill-name>.md`，发布到 `https://aihero.dev/skills-<skill-name>`）。四节必备结构：**What it does**、**When to reach for it**、**Common questions**、**It's working if**。

动机：这些 skills 大多是 user-invoked，agent 永远不会替你触发它们，所以**你是必须记住它们存在的索引**——这份记忆是认知负担（cognitive load）。文档页的职责就是减轻它。

## 工作流程全景图（ask-matt 地图）

`/ask-matt` 是整个体系的**路由器**——"You don't remember every skill, so ask."（你不记得每个 skill，所以问它）。它把整个体系组织成一张地图。这是理解全仓最快的入口。

### 主流程：idea → ship（3 步）

1. `/grill-with-docs` — 访谈打磨想法。**有工作目录时从这里开始**（有状态，把学到的东西存在 `CONTEXT.md` 和 ADR 里）。无目录用 `/grill-me`。两者跑的是同一个底层访谈工具 `/grilling`，`grill-with-docs` 是留下纸面痕迹的那个，所以在仓库里用时严格更优。
2. **分支：能否在对话里解决每个问题？** 若问题需要可运行答案（状态、业务逻辑、要看的 UI），绕道 prototype，双向用 `/handoff` 桥接：
   - `/handoff` 出去 → 开新会话 → `/prototype` 用一次性代码回答 → `/handoff` 把学到的带回来
3. **分支：是否多会话构建？**
   - **是** → `/to-spec`（转规格）→ `/to-tickets`（拆成带依赖关系的实现任务，标出每个任务的前置任务）→ 每个任务跑 `/implement`，之间 `/clear` 清空上下文
   - **否** → 同窗口直接 `/implement`

无论哪条路，`/implement` 内部驱动 `/tdd`（一次一个小功能地红绿推进），收尾跑 `/code-review`（双轴评审）再提交。

### 两条入口（on-ramps）

- **Bug 和需求堆积 →** `/triage`：把**外部流入**的 issue/PR 走状态机，产出"agent 可直接开干"的 issue 供 `/implement` 拾取。**"Triage is only for issues you didn't create"**——`/to-tickets` 产出的已经可以直接开干，别再去 triage 它们。
- **Something's broken →** `/diagnosing-bugs`：拒绝在建立**紧反馈回路**（"one command that already goes red on this bug"）前空谈理论。事后发现"找不到合适的测试入口来锁定 bug"时移交给 `/improve-codebase-architecture`。

### 巨任务入口：`/wayfinder`

**"A huge, foggy effort"**——全新项目（greenfield）或超大功能，超过一个 agent 会话能容纳的——用 `/wayfinder`。它是全套里最费脑的流程。在 issue tracker 上画出**共享地图**的决策 tickets，逐个解析——产出 **决策，而不是交付物**（decisions, not deliverables）。地图清空时**它交接、不构建**：并入主流程的 `/to-spec`。

### 阶段边界决策树（Phase boundaries）

会话内两块工作（如访谈 → 实现）之间的间隙，是唯一允许做这个决策的地方。五个选项，自上而下第一个"是"生效：

1. **Continue（继续）** — 留在本会话。成本为零、无损，**最先排除它**（下一阶段需要本阶段的完整思考过程时选它）
2. `/clear`（清空） — 清空上下文，当所有内容都与接下来无关时。"the cheapest move on the board"（这是棋盘上最便宜的一步）
3. `/handoff`（交接） — 写一份可带走的 markdown 文档。只用于换 AI 工具、换目录、交给同事、或中途分叉一个副任务。"What handoff buys is portability"（交接买来的是可移植性）
4. **Subagent（子代理）** — 任务够紧、无需你掌舵时，送进自己的窗口拿报告回来
5. `/compact`（压缩） — 压缩上下文并用摘要开新会话。**"the default, not the first reach"**（是默认选项，但不是第一选择）——垫底是因为上面四个问题都更便宜或更精确

### 上下文卫生（Context hygiene）

步骤 1–3 保持**一个不中断的上下文窗口**——到 `/to-tickets` 之后才压缩/清空，让访谈、spec、tickets 都建立在同一思考上。上限是 **推理清晰区（smart zone，约 150k tokens）**——超过这个量模型推理会变差，接近时在最近的阶段边界处 `/compact`，不要硬撑退化。

## AI 辅助开发完整步骤

把上述流程展开成一份可操作的开发手册：

### 步骤 1：对齐需求（每次改动都做）

`/grill-with-docs`。Agent 会：

- 先**探索代码库**而非直接问你（能读代码回答的就不占用你的时间）
- 加载 `CONTEXT.md` 建立上下文
- **一轮一轮问问题**，每轮问完当前能问的所有问题（即"现在就能问、不用猜答案"的问题），每个问题**给出推荐答案**（"No-one knows exactly what they want"——主动推荐是减少对齐摩擦最有效的方式）
- 术语确定后**立即内联更新** `CONTEXT.md`，不批量处理
- 满足三条件的决策写 ADR

### 步骤 2：绕道原型（可选）

当问题需要"跑起来才能判断"（状态模型、UI 形态），用 `/prototype`：

- **LOGIC 分支**（"逻辑/状态模型感觉对吗？"）→ 单个可分享 HTML 文件，推动状态机走难推理的用例
- **UI 分支**（"应该长什么样？"）→ 同一路由上 3 个结构不同的 UI 变体，`?variant=` 切换

**纪律**：一次性代码、无测试、无持久化、状态常显、答完吸收进真实代码、原型作为一手资料存临时分支。

### 步骤 3：规格化

`/to-spec`——**不做访谈**，只综合你已讨论的内容。产出**规格说明书（spec）**（含问题陈述 / 解决方案 / 用户故事 / 实现决策 / 测试决策 / 范围外），发布到**问题跟踪器（issue tracker）**，打 `ready-for-agent` 标签。

**关键设计**：**先定测试入口（seam）**——在 spec 阶段就确定测试从哪个公共接口切入被测代码："Use the highest seam possible. The fewer seams across the codebase, the better - the ideal number is one."（测试入口选得越高越好，全代码库的测试入口越少越好，理想数量是一个。）入口要和用户核对。

### 步骤 4：拆票

`/to-tickets`——把 spec 拆成一个个**完整的小功能**（每个都贯穿数据库、接口、界面、测试，能独立验收），每个功能声明它**依赖哪些前置功能**（blocking edges）。

完整小功能的拆分规则：

- 每个小功能**贯穿所有层**（数据库结构、接口、界面、测试）——是完整的端到端功能，不是只做某一层
- 完成的小功能**可独立演示/验证**
- 每个小功能**能装进一个全新的上下文窗口**（约 100K token）
- 预重构先行（"Make the change easy, then make the easy change."）

**大范围重构例外**：横跨全代码库的机械变更（如改列名）不适合拆成小功能一步步来，改用**先扩后缩**两步走：先扩——同时保留新旧两种形式，什么都不破坏 → 按影响范围分批迁移，每批保持不出错 → 最后缩——删掉旧形式。

发布后先做**现在就能开工的任务**（所有前置功能已完成的那批）。

### 步骤 5：实现

`/implement`——每个 ticket 一个会话，流程固定：

1. 用 `/tdd` 在预先商定的测试入口上红绿循环
2. 高频跑类型检查、单测；收尾跑全量测试
3. 用 `/code-review` 双轴评审
4. commit

`/tdd` 的核心纪律：

- **先红后绿**：先写失败测试，再只写足够让它通过的代码。不预判未来测试、不加投机功能
- **一次一个小功能**：一个测试入口、一个测试、一个最小实现
- **重构不属于循环**：重构属于 review 阶段，不是红绿实现循环
- **反模式**：耦合实现细节（测内部协作者/私有方法，重构就挂）、同义反复（期望值按代码同样的方式重算，永远测不出错）、按层拆分（先批量写完所有层的测试再实现，测的是想象中的行为）

### 步骤 6：代码评审

`/code-review`——**双轴并行评审**：

- **Standards 轴**：是否符合仓库编码标准 + Fowler 12 种代码味道基线
- **Spec 轴**：是否忠实实现起源 issue/spec

两轴作为并行的子代理（sub-agent）运行，避免互相污染上下文，**不合并不重排**分开展示。"A change can pass one axis and fail the other"（一个改动可能通过一轴却败在另一轴）正是它存在的理由。

### 日常维护

- **每几天**：`/improve-codebase-architecture` 扫描"加深机会"（把浅模块改造成深模块的候选，产出 HTML 可视化报告，选出候选再访谈）
- **有 bug**：`/diagnosing-bugs` 六阶段纪律
- **外部 issue 堆积**：`/triage` 状态机
- **合并冲突**：`/resolving-merge-conflicts` 按意图解决，从不 `--abort`

## 核心 skill 逐个详解

### `/grilling` — 访谈底层工具（一切问答的地基）

不是独立使用的 skill，而是被 `grill-me`、`grill-with-docs`、`triage`、`wayfinder`、`improve-codebase-architecture` 内部调用的**基础组件**。

工作机制：

- 把计划映射为**设计树**：每个决定分支为挂在其下的决定
- 按 **轮次（rounds）** 工作：**当前能问的问题** = 前置条件已经解决、现在就能问的问题（"the questions you can ask now without guessing at answers you haven't heard yet"）
- 一轮问完所有当前能问的问题，每个问题**编号 + 给出推荐答案**，然后等用户回答再下一轮
- **找事实是你的工作（agent），决定是用户的**：当前要问的问题需要环境事实时派 sub-agent 查，不麻烦用户
- **结束条件**："The session is done when the frontier is empty: every branch of the design tree visited, nothing left silently assumed."（能问的都问完了，每个分支都访问过，没有东西被默默假设）

### `/grill-with-docs` — 对话驱动的文档同步

全文只有一句指令："Run a `/grilling` session, using the `/domain-modeling` skill."——在访谈过程中同时做领域建模。它是 grilling + domain-modeling 的薄封装，区别在于**有状态**（写 `CONTEXT.md`/ADR）。

### `/domain-modeling` — 主动领域建模

会话中五件事：

1. **对照词汇表挑战**：用户用词与 `CONTEXT.md` 冲突时立刻指出（"Your glossary defines 'cancellation' as X, but you seem to mean Y — which is it?"）
2. **锐化模糊语言**（"You're saying 'account' — do you mean the Customer or the User?"）
3. **讨论具体场景**：用探边界的场景压力测试领域关系
4. **与代码交叉验证**：用户说的和代码不一致时浮出矛盾
5. **内联更新** `CONTEXT.md`：术语一确定立即写，不批量

### `/tdd` — 红绿重构

- 核心：**"Test only at pre-agreed seams"**——写测试前先和用户确认测试从哪个入口切入。没确认的测试入口不写测试
- 好测试：通过公共接口验证行为，"A good test reads like a specification"（好的测试读起来像规格说明），重构后仍通过
- 坏测试：耦合实现细节，重命名内部函数就失败
- 判断标准：**"如果你只做内部重构（不改外部行为），测试还会失败吗？"** 如果会——这是实现测试，不是行为测试

### `/diagnosing-bugs` — 六阶段诊断

1. **Phase 1：建立反馈回路**——"**This is the skill.** Everything else is mechanical."（这就是核心，其余都是机械动作）。先有能明确判断"对/错"的通过失败信号，再谈假设。10 种构造方式（失败测试、curl、CLI、无头浏览器、重放 trace、一次性 harness、fuzz、bisection、differential、人机配合脚本）。"Build the right feedback loop, and the bug is 90% fixed."
2. **Phase 2：复现 + 最小化**——确认是用户描述的 bug，逐一切掉直到每个元素都 load-bearing
3. **Phase 3：假设**——列 3–5 个**可证伪**假设：格式是"如果 <X> 是根因，那么 <改动 Y> 会让 bug 消失 / <改动 Z> 会让 bug 更严重"。写不出这种预测的就是"直觉"而不是"假设"，直接丢弃或精化。先给用户看排序列表
4. **Phase 4：插桩**——一次改一个变量；调试器 > 定向日志 > 绝不"log everything and grep"；日志带 `[DEBUG-xxxx]` 前缀便于清理
5. **Phase 5：修复 + 回归测试**——先写回归测试再改代码，但**只在存在合适的测试入口时**。"If no correct seam exists, that itself is the finding"（找不到合适的测试入口本身就是发现——架构在阻止锁定这个 bug）
6. **Phase 6：清理 + 复盘**——删 debug 日志、验证原复现消失、正确的假设写进 commit message。**然后问："what would have prevented this bug?"** 若答案是架构性的，移交给 `/improve-codebase-architecture`

### `/to-spec` — 规格化（不做访谈）

明确"**Do NOT interview the user — just synthesize what you already know.**"关键设计是**先定测试入口** + 不写文件路径/代码片段（容易过时）。例外：prototype 产出的能编码决策的片段（state machine、reducer、schema、type shape）可内联并注明来源。

### `/to-tickets` — 拆票

完整小功能拆分 + 任务间的依赖关系 + 大范围重构的"先扩后缩"例外。"A completed slice is demoable or verifiable on its own"（每个小功能都能独立演示验证）/ "Each slice is sized to fit in a single fresh context window"（每个小功能都能装进一个全新的上下文窗口）。发布到 tracker 后默认打 `ready-for-agent`——"the tickets are agent-grabbable by construction"（任务天生就是 agent 可领取的）。

### `/implement` — 实现的编排节点

全套里最薄的一份（全文约 15 行），纯编排：

1. Use `/tdd` where possible, at pre-agreed seams（尽量用 TDD，在预先商定的测试入口处）
2. Run typechecking regularly, single test files regularly, and the full test suite once at the end（高频跑类型检查和单个测试文件，最后跑一次全量测试）
3. Once done, use `/code-review` to review the work
4. Commit your work to the current branch

### `/code-review` — 双轴评审

- Standards 轴 + Spec 轴并行 sub-agent，不合并不重排
- 12 种 Fowler 代码味道基线：Mysterious Name、Duplicated Code、Feature Envy、Data Clumps、Primitive Obsession、Repeated Switches、Shotgun Surgery、Divergent Change、Speculative Generality、Message Chains、Middle Man、Refused Bequest
- "The repo overrides"（仓库文档标准胜出）+ "Always a judgement call"（味道是判断不是硬违规）

### `/triage` — Issue 状态机

外部流入的 issue/PR 走状态机。**分类角色（Category）**（bug 缺陷 / enhancement 增强）× **状态角色（State）**（needs-triage 待分诊 / needs-info 缺信息 / ready-for-agent 可交给代理 / ready-for-human 需人工 / wontfix 不处理）。

最有意思的设计是 **ready-for-agent**：打这个标签时，agent 要写一份**任务简报（agent brief）**——"It is the authoritative specification that an AFK agent will work from"（无人值守的 agent 依据它来干活，它是权威规格；issue 原始内容只是背景，brief 才是契约）。四原则：durability over precision（描述接口/类型/行为契约，不引用文件路径行号）、behavioral not procedural（描述"应该做什么"而非"怎么实现"）、complete acceptance criteria（完整的验收标准）、explicit scope boundaries（明确的范围边界）。

所有 triage 评论必须以这行开头：`> *This was generated by AI during triage.*`——透明度设计，区分人写的和 AI 写的。

### `/wayfinder` — 巨任务的决策地图

- "**Plan, don't do**"——产出 decisions not deliverables
- 地图是 tracker 上标 `wayfinder:map` 的单条 issue，子 tickets 是子 issue
- **Fog of war（战争迷雾）**：地图故意不完整。**"Fog or ticket? The test is whether you can state the question precisely now — not whether you can answer it now."**（能精确陈述问题就建 ticket，还不能就留在迷雾区）
- Ticket 类型：Research（无人值守 AFK，`/research` 子代理）/ Prototype（人机配合 HITL）/ Grilling（人机配合 HITL，默认）/ Task（人机配合或无人值守，先做事以解锁决策）
- **每个会话只解一个 ticket**（research 除外）
- 地图清空后**交给** `/to-spec`，不直接 build

### `/prototype` — 一次性原型

"A prototype is **throwaway code that answers a question**. The question decides the shape." 关键纠偏："Throwaway is a constraint on how the code is written, not a promise to destroy it"——答案折进真实代码，原型本身作为一手资料存临时分支。

### `/research` — 后台研究

起一个**后台代理（background agent）**——你继续干活，它在后台读资料。只对**一手资料**（官方文档、源码、spec、第一方 API）调查，产出带引用的 md 文件。"research feeds the thinking, it doesn't replace it"（研究喂养思考，不替代思考）。

### 其他值得知道的

- `/wizard` — 生成交互式 bash 向导，走"只有人类能做的步骤"（配基础设施、设凭据、走第三方面板）。"Don't invoke this for steps the agent can perform itself."
- `/handoff` — 把当前对话压成交接文档（存操作系统临时目录，含"建议使用的 skills"段，脱敏，引用已有产物而非重复内容）
- `/wait-what` — "Stop. That last message did not land — re-pitch it." 消息没落地时的纠正器
- `/to-questionnaire` — "Grill the send, not the subject."（拷问发送，不拷问主题）——把决定变成别人填的问卷
- `/resolving-merge-conflicts` — 按**意图**逐 hunk 解决冲突，"Always resolve; never `--abort`"
- `/codebase-design` — 深模块（deep module：小接口、大实现的模块）词汇层：**删除测试**（deletion test：删掉这个模块，复杂度是跟着消失，还是散落到各处？后者才说明它值得存在）、"One adapter means a hypothetical seam. Two adapters means a real one."（只有一个适配器时接口是假设的，出现两个适配器才说明这个接口真实有价值）
- `/teach` — 用当前目录作为有状态教学工作区，多会话教用户新技能
- `/writing-for-agents` — 写 agent 消费文档的参考（context pointers、leading words、progressive disclosure）

## 对我（用户）的启示与借鉴

### 1. 用 grilling 强制对齐

**每次开始一个需求，先让 AI 拷问你**，而不是直接丢需求让它做。重点不是 AI 问了多少问题，而是：

- **一轮问当前能问的所有问题，每个带推荐答案**（减少摩擦）
- 能读代码回答的**不让 AI 来问你**（找事实是 agent 的职责）
- **直到没有待问的问题**（没有东西被默默假设）才动手

这直接解决"等了 10 分钟看到一大片不符合意图的代码"。

### 2. 建立你自己的域语言（CONTEXT.md）

**给每个项目维护一份术语表**，格式：术语 + 定义 + `_Avoid_:` 禁止词。当 AI 用了冲突的术语立刻纠正它。收益：

- 沟通更简洁（"物化级联" vs "课程里某节课被变成真实的那个问题"）
- 命名一致 → 代码库（codebase）更易导航
- AI 思考 token 更省

### 3. 把纪律固化为不可跳过的步骤

TDD、ADR、反馈回路这些纪律人容易偷懒跳过。**让 AI 强制执行**：

- 先写失败测试再实现（红绿）
- 一次一个小功能（贯穿所有层的完整功能），不批量写测试再实现
- 重构归 review，不混在实现里

### 4. Feedback loop 优先于理论

**没有可复现的 pass/fail 信号，不要谈假设。** 无论是写功能还是修 bug，先建反馈回路（"Build the right feedback loop, and the bug is 90% fixed"）。调试时先复现 + 最小化，再列可证伪假设，一次改一个变量。

### 5. 管理阶段边界和上下文

- 大任务拆成**每个能装进一个上下文窗口的小功能**，之间 `/clear`
- 规划（grill→spec→tickets）放一个**不中断的窗口**
- 需要换目录/换工具/交接时用 handoff 文档，需要压缩时在**阶段边界**处 `/compact`

### 6. 关注代码设计，对抗 AI 加速的熵

- 用**删除测试**判断模块是否值得存在
- 追求**深模块**：小接口 + 大实现
- 定期（每几天）扫描架构加深机会

### 7. 借鉴"触发方式"设计

**哪个 skill 该由人触发、哪个该由模型自动抓取，是刻意的设计**：编排类的由人触发（保持控制权），纪律类的由模型自动抓取。description 的写法决定触发准确度——面向人的写一行摘要，面向模型的写富触发措辞。

## 局限与注意

- **无机器化验收测试**：`/setup-matt-pocock-skills` 等正确性依赖人工验证，很难做 CI
- **Markdown 表达上限**：复杂条件逻辑（如 diagnosing-bugs Phase 5 的"只有存在合适的测试入口才写回归测试"）需要多段自然语言描述，换成代码可能更精确——但那就不是 Markdown 了。这是这类方案的内在张力
- **跨仓库配置需重复初始化**：每个仓库都要独立跑 `/setup-matt-pocock-skills`，没有全局配置继承
- **Skill 质量依赖作者经验**：没有机器化验收标准，"这个 skill 是否让 agent 做出正确行为"只能靠反复使用判断
- **仓库持续演进**：skill 会改名、新增、删除（本文基于 v1.2.3）。参考笔记可能迅速过时，以当前仓库为准
- **主要面向 GitHub/TypeScript 生态**：本地 markdown tracker 开箱即用，但深度集成（原生 blocking links）依赖 GitHub/GitLab 的 Premium 特性

## 总结

Matt Pocock 的 Skills 体系揭示了一个重要洞见：**AI Agent 的质量上限，不是模型能力，而是你给它的工程流程框架。**

把 DDD 域语言、TDD 红绿重构、ADR 决策记录这些经过验证的软件工程实践，蒸馏成 Agent 可消费的结构化 skill，是非常务实的方向。它不试图发明新的方法论，而是让现有方法论在 AI 时代继续有效。

**最值得借鉴的三点**：

1. **用 CONTEXT.md 建立域语言共享**——让 agent 说你项目的"行话"，消除沟通摩擦
2. **Hard / Soft 依赖分层**——按依赖程度区别对待，避免过度耦合，保持 skill token-light
3. **把纪律性流程（TDD、ADR）固化为 skill**——利用 AI 的执行一致性，弥补人的纪律性不足

软件工程的基本原则在 AI 时代不仅没有过时，反而更加重要——因为 AI 可以以前所未有的速度积累技术债，只有把工程纪律内化为不可跳过的 skill，才能在速度和质量之间保持平衡。

