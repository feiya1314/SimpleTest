# Superpowers 仓库深度分析

> 分析对象:https://github.com/obra/superpowers(当前版本 v6.3.0,2026-08-12 发布)
> 分析日期:2026-08-17
> 分析范围:仓库工作流、全部 skills、subagents、hooks、rules 的作用与设计原因

---

## 目录

1. [项目概述与核心定位](#一项目概述与核心定位)
2. [仓库结构总览](#二仓库结构总览)
3. [核心机制:技能如何自动触发](#三核心机制技能如何自动触发)
4. [14 个技能逐一详解](#四14-个技能逐一详解)
5. [完整工作流:从想法到合入分支](#五完整工作流从想法到合入分支)
6. [Subagents:子代理体系](#六subagents子代理体系)
7. [多 Harness 集成机制](#七多-harness-集成机制)
8. [Rules 与配置体系](#八rules-与配置体系)
9. [测试与评估体系](#九测试与评估体系)
10. [版本演进历史](#十版本演进历史)
11. [设计哲学:为什么这样设计](#十一设计哲学为什么这样设计)
12. [贡献指南](#十二贡献指南)

---

## 一、项目概述与核心定位

### 1.1 一句话定位

> **Superpowers 是一套完整的软件开发方法论(software development methodology),构建在一组可组合的技能(skills)之上,外加一些确保 agent 使用这些技能的启动指令(initial instructions / bootstrapping)。**

它不是一个"技能"或"工具",而是一整套**改变 AI 编程 agent 行为方式的工作流系统**。核心思想:agent 启动的那一刻起,不是立刻写代码,而是先理解你在做什么、设计、写计划、然后按计划执行。

### 1.2 作者与团队

- 作者:**Jesse Vincent**(博客 blog.fsck.com),及其所在的 **Prime Radiant** 公司团队
- 社区:Discord、GitHub Issues(https://github.com/obra/superpowers/issues)
- 商业化:提供企业支持服务(sales@primeradiant.com)
- 原始发布公告:https://blog.fsck.com/2025/10/09/superpowers/

### 1.3 三大核心概念

| 概念 | 含义 |
|---|---|
| **Skill(技能)** | 塑造 agent 行为的指令单元,存于 `skills/<name>/SKILL.md`,含 YAML frontmatter(name + description)与正文 |
| **Bootstrap(引导)** | `using-superpowers` 技能的内容,在**每个会话开始时**被注入模型上下文,包在 `<EXTREMELY_IMPORTANT>` 标签中。它是整个集成的关键——没有它,技能文件只是"死重量"(present on disk but never invoked) |
| **"your human partner"(你的人类伙伴)** | 项目刻意使用 "your human partner" 而非 "the user"。CLAUDE.md 明确这是**刻意选择的术语,不可替换**——体现人与 agent 的合作关系而非命令-服从关系 |

### 1.4 设计哲学(README Philosophy)

- **Test-Driven Development** — 永远先写测试
- **Systematic over ad-hoc** — 流程优于猜测(系统化胜过即兴)
- **Complexity reduction** — 简单是首要目标(YAGNI、DRY)
- **Evidence over claims** — 验证后再宣布成功

### 1.5 一个关键句

README 中的 "The Basic Workflow" 结尾:**"The agent checks for relevant skills before any task. Mandatory workflows, not suggestions."**
—— agent 在任何任务前检查相关技能;**是强制工作流,不是建议**。

---

## 二、仓库结构总览

```
superpowers/
├── README.md                  # 项目说明:工作流、安装、哲学
├── CLAUDE.md                  # 贡献者指南(AGENTS.md 软链指向它)
├── GEMINI.md                  # Gemini 扩展入口(两个 @-include)
├── RELEASE-NOTES.md           # 1400 行版本发布说明(v2.0.1 → v6.3.0)
├── LICENSE                    # MIT
├── package.json               # main 指向 .opencode 插件,pi 字段声明
├── .version-bump.json         # 版本同步的声明文件(9 处版本字段)
├── .pre-commit-config.yaml    # 只针对 evals/ 的 Python 质量门
├── .gitattributes / .gitignore
│
├── skills/                    # ★ 14 个技能(核心资产,唯一事实来源)
│   ├── using-superpowers/     #   引导技能 + references/ 工具映射
│   ├── brainstorming/         #   三路径分类 + 视觉同伴 + scripts/
│   ├── writing-plans/         #   计划编写
│   ├── subagent-driven-development/  # SDD(最复杂)+ 3 个 prompt 模板 + scripts/
│   ├── executing-plans/       #   内联执行器(SDD 轻量替代)
│   ├── test-driven-development/      # TDD
│   ├── systematic-debugging/  #   系统化调试 + 3 个技术参考 + find-polluter.sh
│   ├── verification-before-completion/  # 完成前验证
│   ├── requesting-code-review / receiving-code-review  # 评审闭环
│   ├── dispatching-parallel-agents/    # 并行派发
│   ├── using-git-worktrees/   #   隔离工作区
│   ├── finishing-a-development-branch/ # 完成分支
│   └── writing-skills/        #   元技能:如何编写技能
│
├── hooks/                     # 会话引导钩子
│   ├── hooks.json             #   Claude Code hook 配置(SessionStart)
│   ├── hooks-cursor.json      #   Cursor hook 配置
│   ├── run-hook.cmd           #   跨平台 polyglot 分发器
│   └── session-start          #   真正的引导脚本(注入 bootstrap)
│
├── scripts/                   # 工程脚本
│   ├── bump-version.sh        #   版本同步
│   ├── lint-shell.sh          #   ShellCheck
│   ├── package-codex-plugin.sh #  Codex 分发包构建
│   └── sync-to-codex-plugin.sh #  Codex fork 同步
│
├── docs/
│   ├── porting-to-a-new-harness.md  # ★ 移植权威指南(三种集成形态)
│   ├── testing.md             #   测试哲学
│   ├── README.kimi.md / README.opencode.md
│   ├── windows/polyglot-hooks.md    # Windows 跨平台方案
│   ├── superpowers/specs/     #   设计文档(19 份)
│   └── superpowers/plans/     #   实现计划(20 份)
│
├── .<harness>-plugin/ 或 .<harness>/  # 每 harness 的 manifest/入口
│   ├── .claude-plugin/        #   Claude Code 插件清单
│   ├── .codex-plugin/         #   Codex 插件清单
│   ├── .cursor-plugin/        #   Cursor 插件清单
│   ├── .devin-plugin/         #   Devin 插件清单
│   ├── .hermes-plugin/        #   Hermes 插件(plugin.yaml + __init__.py)
│   ├── .kimi-plugin/          #   Kimi 插件清单
│   ├── .opencode/             #   OpenCode 插件(JS)
│   └── .pi/                   #   Pi 扩展(TS)
│
├── tests/                     # 插件基础设施测试(非 LLM 代码)
├── evals/                     # superpowers-evals 克隆(gitignore 排除)
├── .github/                   # PR 模板 + 3 个 issue 模板
└── assets/                    # 图标(logo)
```

---

## 三、核心机制:技能如何自动触发

### 3.1 完整链路

```
Claude Code 会话启动(/startup、/clear、/compact)
    │
    ▼
hooks/hooks.json 注册的 SessionStart hook 被触发
    │  matcher: "startup|clear|compact"(每次上下文重置都注入)
    ▼
运行 hooks/run-hook.cmd session-start
    │  (Windows 上定位 Git Bash;Unix 直接执行)
    ▼
hooks/session-start 脚本执行:
    │  1. 读取 skills/using-superpowers/SKILL.md 全文
    │  2. 用 bash 参数替换做 JSON 转义
    │  3. 包进 <EXTREMELY_IMPORTANT> 标签组装注入文本
    │  4. 按环境变量输出三种 JSON 形状之一:
    │     - Claude Code  →  hookSpecificOutput.additionalContext
    │     - Cursor       →  additional_context(顶层 snake_case)
    │     - Copilot CLI  →  additionalContext(顶层 camelCase,SDK 标准)
    ▼
注入文本进入会话上下文:
    "You have superpowers. Below is the full content of your
     'superpowers:using-superpowers' skill - your introduction to using
     skills. For all other skills, use the 'Skill' tool:"
    + 完整 SKILL.md 内容
    ▼
模型从此"拥有 superpowers",遵守其中的铁律:
    "哪怕有 1% 可能某个技能适用,就必须调用技能"
    "在任何回应或行动之前(包括澄清问题)先调用相关技能"
    ▼
用户说 "Let's build X" → using-superpowers 的 Skill Priority 规则
    → 模型调用 Skill 工具加载 brainstorming → 强制进入设计流程
```

### 3.2 为什么必须注入而非依赖文件存在

- 技能文件只是磁盘上的**死文件**。只有 `SKILL.md` 进了上下文,模型才会去找技能。
- **"The bootstrap is the entire integration"**(引导就是整个集成)。没有引导,技能"present on disk but never invoked"。
- 因此每种 harness 的第一要务,都是把这份内容(包 `<EXTREMELY_IMPORTANT>` 标签 + 工具映射)送到模型上下文。

### 3.3 引导如何"自举"而不列出技能清单

`using-superpowers/SKILL.md` 本身**不含技能清单**。它只告诉模型:
1. 存在技能系统,开始任何事之前先检查技能
2. 各技能靠**自身的 description 前置触发**——模型读到 "Use when..." 就触发(Agent 会扫描技能列表)

即:技能的"可发现性"完全依赖每个技能 description 里精确的触发词设计(见 writing-skills 的 SDO 章节)。

### 3.4 为什么注入成上下文而不是系统消息

- 系统消息逐轮重复会**token 膨胀**(issue #750)
- 多系统消息会破坏某些模型(issue #894)
- 因此 Shape B(进程内插件)把 bootstrap 作为**第一条 user 消息**插入,用 `<EXTREMELY_IMPORTANT>` 标记做去重守卫

### 3.5 三种集成形态(Shape A/B/C)

根据 `docs/porting-to-a-new-harness.md`,集成机制按"bootstrap 如何到达模型上下文"分成三种结构形态:

| 形态 | 机制 | 代表 harness |
|---|---|---|
| **Shape A 外壳 Hook** | 会话开始时运行 shell 命令,读取其 stdout(JSON)注入上下文 | Claude Code、Cursor、Copilot CLI、Antigravity |
| **Shape B 进程内插件** | 加载 JS/TS/Python 模块,生命周期回调改写消息数组注入 bootstrap | OpenCode、Pi、Hermes Agent |
| **Shape C 指令文件** | 扩展自带上下文文件,manifest 声明 `contextFileName`,harness 每次会话加载 | Gemini CLI |

**特殊案例**:Codex(原生技能发现 + 空 `hooks` 对象抑制自动发现)、Kimi Code(manifest 级 `sessionStart.skill`)、Devin CLI(surfaced skill index,最弱形态)。

**硬性要求**:每个会话开始必须能自动注入,无需人工 opt-in。**验收测试**:干净会话中发送 `Let's make a react todo list`,必须在写任何代码之前自动触发 brainstorming 技能。

---

## 四、14 个技能逐一详解

技能体系全景图:

```
using-superpowers(引导/元技能)
   │  SessionStart hook 注入,要求所有响应前先检查技能
   ▼
brainstorming(把想法打磨成设计/规格)
   ▼
writing-plans(把规格写成可执行计划)
   ▼
subagent-driven-development(推荐执行器,每任务派发 subagent + 评审)
   └─(备选)executing-plans(同会话内联执行,带检查点)
   └─ 依赖:using-git-worktrees(隔离工作区)
       └─ 依赖:dispatching-parallel-agents(任务内并行派发独立调查)
   └─ 依赖:test-driven-development(实现方法论)
       └─ 触发:systematic-debugging(任何 bug)
       └─ 前置:verification-before-completion(完成前验证)
       └─ 配套:requesting-code-review / receiving-code-review(评审闭环)
   ▼
finishing-a-development-branch(测试 → 菜单 → 合入/PR/保留)
```

### 4.1 using-superpowers —— 引导技能(Bootstrap)

| 项目 | 内容 |
|---|---|
| **名称** | `using-superpowers` |
| **description** | "Use when starting any conversation - establishes how to find and use skills, requiring skill invocation before ANY response including clarifying questions" |
| **文件** | `SKILL.md`(63 行)+ `references/`(5 个平台工具映射文件) |
| **定位** | 会话入口的元技能,确立"先找技能、再行动"的纪律,让所有其他技能得以被自动触发 |

**核心内容与设计**:

1. **`<SUBAGENT-STOP>`**:被派发为 subagent 执行具体任务时**忽略本技能**(防止子代理递归加载流程技能)。
2. **`<EXTREMELY-IMPORTANT>` 规则**:哪怕只有 1% 可能适用,就必须调用技能;"IF A SKILL APPLIES TO YOUR TASK, YOU DO NOT HAVE A CHOICE. YOU MUST USE IT." 禁止自我合理化绕过。
3. **The Rule**:在任何回应或行动**之前**(包括澄清问题、探索代码库、检查文件)先调用相关技能;进入 plan mode 前若没头脑风暴先调用 brainstorming;然后**宣布**"Using [skill] to [purpose]"并严格遵循;技能带 checklist 则每项建一个 todo。
4. **Skill Priority(技能优先级)**:多个技能适用时,**流程性技能(process skills)优先**——它们决定方法,实现性技能随后执行:
   - "Let's build X" → brainstorming 优先,然后才是实现技能
   - "Fix this bug" → systematic-debugging 优先,然后才是领域技能
5. **Red Flags 表**:12 条"合理化借口 → 现实"对照(如"这只是个简单问题"→ 问题也是任务,要检查技能;"我记着这个技能"→ 技能在演进,读当前版本)。
6. **Platform Adaptation**:按 harness(Codex/Pi/Antigravity/Hermes)读取 `references/` 下的工具映射参考。
7. **User Instructions**:用户指令文件(CLAUDE.md 等)与用户明确要求优先于技能,技能优先于默认行为;只有人类伙伴明确说跳过时才可跳过。

**设计要点**:用"铁律 + 极端重要性声明 + Red Flags 表(反合理化)+ 宣布机制(利用承诺心理原则)"来对抗"代理跳过技能"这一最普遍的失败模式。这是整个系统的**总开关**。

---

### 4.2 brainstorming —— 想法 → 设计/规格

| 项目 | 内容 |
|---|---|
| **名称** | `brainstorming` |
| **description** | "You MUST use this before any creative work - creating features, building components, adding functionality, or modifying behavior. Explores user intent, requirements and design before implementation." |
| **文件** | `SKILL.md`(250 行)+ `visual-companion.md`(视觉同伴)+ `spec-document-reviewer-prompt.md`(规格审查模板)+ `scripts/`(零依赖 Node 服务器) |
| **定位** | 任何创造性工作前的必经之门,通过对话把想法打磨成设计并取得人类伙伴批准 |

**核心:三路径分类器(Three Paths)** —— v6.3.0 引入,让"仪式感"随任务缩放:

- **Spike(探针)**:可行性问题("can we...", "is it possible..."),输出是**答案**而非保留的代码。2-3 句话呈现问题+探测计划 → 点头 → 尽量便宜地调查 → 报告建议;任何构建物标注为 throwaway(一次性)。无设计文档、无 spec 文件。
- **Bounded(有界)**:对仓库中**已存在代码**的边界清晰改动(新 flag、小端点、单文件修复)。判断标准是**仓库里有没有现成可改的流程**(不是你对这类应用熟悉)。问必要澄清问题 → 在聊天中呈现简短设计 → **停下等批准**。无 spec 文件、无计划文档。
- **Architectural(架构级)**:新项目、新子系统、重构组件关系或改变他人依赖的接口。走完整流程:提问 → 方案 → 分节设计 → 书面 spec → writing-plans 技能。

**棘轮规则(Ratchet)**:两难时走更重的那条路。棘轮是**单向的**——中途发现隐藏复杂度只能**升级**路径(停下、说出来、升档),永不降级。

**HARD-GATE(硬闸门)**:
> 在告诉人类伙伴意图并获批准之前,不得调用任何实现技能、写代码、搭脚手架或采取任何实现动作。适用于每条路径每个任务。**仪式随任务缩放;批准闸门从不缩放。**

**反模式:"Too Simple To Need Approval"**:每条路径都以人类伙伴批准意图结尾。todo 列表、单函数工具、配置改动——设计可以只有聊天里两句话,但**必须呈现并获批**。简单任务是最容易埋藏未审视假设的地方。

**Architectural 路径流程**:
1. 探索项目上下文(文件、文档、最近提交)
2. **(刚好及时)提供视觉同伴**——绝不在一开始就提
3. 逐个澄清问题(一次一个问题、优先多选题,聚焦 purpose/constraints/success criteria)
4. 提 2-3 个方案 + 权衡 + 你的推荐
5. 分节呈现设计、逐节获批
6. 写设计文档到 `docs/superpowers/specs/YYYY-MM-DD-<topic>-design.md` 并提交
7. Spec 自审(占位符扫描/内部一致性/范围/歧义)
8. **用户审阅书面 spec 并等待批复**
9. 调用 writing-plans(唯一允许的下一步技能)

**终态路径绑定**:Architectural 路径唯一被允许在 brainstorming 之后调用的技能是 **writing-plans**;Bounded 获批后直接走正常开发流程(TDD 适用);Spike 终态是报告建议。

**视觉同伴(Visual Companion)**:可选浏览器工具(不是模式),用于展示 mockups/图表/视觉选项。
- **just-in-time 提供**:直到遇到一个"用展示比用文字更清晰"的问题(真正的 mockup/布局/图表,而非仅仅 UI 话题)才提供,且作为独立消息,等用户回复。
- **逐问题决策**:即使接受,每个问题仍判断"看比读更能理解吗?"——视觉内容用浏览器;文字内容(需求、权衡列表、A/B/C/D 选项)用终端。
- **技术机制**:`start-server.sh --open` 启动零依赖 Node 服务器(`server.cjs`,手写 RFC 6455 WebSocket、会话密钥鉴权 `?key=`、抗 DNS rebinding);agent 向 `screen_dir` 写 HTML 片段;浏览器自动 reload;用户点击 → 事件写入 `state_dir/events`(JSONL),agent 下轮读取。
- **遥测**:默认加载 Prime Radiant logo 含版本号,仅用于统计使用量;可 `SUPERPOWERS_DISABLE_TELEMETRY` 关闭。

**设计要点**:把"设计"从"写代码"中分离,用对话式苏格拉底问答逐步澄清;批准闸门绝不缩放是防止"简单任务未审视假设"的关键。

---

### 4.3 writing-plans —— 把规格写成可执行计划

| 项目 | 内容 |
|---|---|
| **名称** | `writing-plans` |
| **description** | "Use when you have a spec or requirements for a multi-step task, before touching code" |
| **文件** | `SKILL.md` + `plan-document-reviewer-prompt.md`(计划审查模板) |
| **定位** | 面向"零代码库上下文 + 品味存疑的初级工程师"把 spec 拆成 bite-sized 任务的详细实现计划 |

**核心工作流**:

1. **Scope Check**:若 spec 覆盖多个独立子系统,建议拆成多个计划,每个独立产出可工作、可测试的软件。
2. **File Structure(先定文件结构再定任务)**:先映射要创建/修改的文件及各自职责;单元边界清晰、接口明确、每个文件单一职责;一起变化的文件放一起。
3. **Task Right-Sizing**:任务是"自带测试周期 + 值得一个全新评审闸门"的最小单元;只有"评审者能合理否决一个任务而批准其邻居"时才拆分。
4. **Bite-Sized 粒度**:每步是一个动作(2-5 分钟):"写失败测试"→"运行确认失败"→"写最小实现"→"运行确认通过"→"提交"。每步带具体代码块和预期输出。
5. **Plan 头部模板(强制)**:
   - 标题 + `> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (- [ ]) syntax for tracking.`
   - `**Goal:**`(一句话)、`**Architecture:**`(2-3 句)、`**Tech Stack:**`、`**Spec:**`(spec 路径)、`## Global Constraints`(从 spec 逐字复制的项目级约束)
6. **Task 结构模板**:`**Files:**`(Create/Modify/Test 精确路径)、`**Interfaces:**`(Consumes/Produces——精确签名与类型)、TDD 五步 checkbox(带真实代码)。
7. **No Placeholders(绝不写)**:TBD/TODO/"加适当错误处理"/"写上述测试"(不带测试代码)/"类似任务 N"/只有描述没有 how 的步骤。
8. **Self-Review**:① Spec 覆盖度(每条需求能否指到实现它的任务)② 占位符扫描 ③ 类型一致性(跨任务签名一致)。
9. **Execution Handoff**:保存计划后给出二选一——**1. Subagent-Driven(推荐)**(强制 SDD)或 **2. Inline Execution**(强制 executing-plans)。

**配套文件 plan-document-reviewer-prompt.md**:派发计划评审 subagent 的模板,检查完整性、Spec 对齐、任务分解、可构建性。校准:只标记会造成实现期真实问题的项。

**设计要点**:计划是"给零上下文工程师的完全自包含文档";Interfaces 块是跨任务契约的唯一通道;REQUIRED SUB-SKILL 声明是跨技能编排的关键钩子。

---

### 4.4 subagent-driven-development(SDD)—— 核心执行器

| 项目 | 内容 |
|---|---|
| **名称** | `subagent-driven-development` |
| **description** | "Use when executing implementation plans with independent tasks in the current session" |
| **文件** | `SKILL.md`(568 行,最复杂)+ `implementer-prompt.md`、`task-reviewer-prompt.md`、`re-review-prompt.md` 三个模板 + `scripts/`(sdd-workspace、task-brief、review-package) |
| **定位** | 按计划逐任务派发"全新 implementer subagent",每任务后做任务评审(spec 合规 + 代码质量),最后做整分支终审 |

**核心设计理念**:
- **上下文卫生**:控制器保持干净只做协调;手稿走文件、diff 走文件、报告走文件。subagent 绝不继承会话历史,精确构造上下文。
- **Ledger(台账)**:会话压缩后记忆不存活。每个计划拥有 git-ignored 工作区 `.superpowers/sdd/<plan-basename>/`,存放全部产物(ledger、briefs、reports)。台账首行是身份,压缩后**信台账和 git log,不信记忆**。
- **评审是闸门与方向盘**:fix 循环的制动与转向。
- **模型路由是成本杠杆**:每次派发必须显式指定 model;轮数成本 > token 价格。

**Setup**:
1. 用 `using-git-worktrees` 创建隔离工作区;未经同意绝不在 main/master 上实现
2. 用 `scripts/sdd-workspace PLAN_FILE` 建立计划工作区 + ledger
3. 读计划一次,记下 context 和 Global Constraints;计划点名 spec 就读 spec(spec 是权威)
4. **Pre-flight 扫描**:派发 Task 1 前扫描计划找冲突;输出必须是表格,写进台账;执行前裁决所有发现

**模型选择**:用**能做该角色的最弱模型**——机械实现用便宜模型;架构/设计/终审用最强模型;fix 循环第 4-5 轮至少比卡住的实现者高一档。

**任务循环**:
- **步骤 1 派发 implementer**:记录 `BASE=git rev-parse HEAD`;运行 `scripts/task-brief PLAN_FILE N` 提取任务全文到唯一文件;brief 是需求的**唯一来源**;精确值只出现在 brief,绝不让 subagent 读整个计划文件;implementer 把完整报告写文件,只回状态/提交/一行测试摘要/担忧;**绝不并行派发多个实现 subagent**。
- **步骤 2 处理报告**:四种状态——`DONE`(进评审)、`DONE_WITH_CONCERNS`(先解决正确性/范围类)、`NEEDS_CONTEXT`(补上下文重派)、`BLOCKED`(判断阻塞:补上下文/更强模型/拆任务/裁决计划)。
- **步骤 3 评审任务**:运行 `scripts/review-package PLAN_FILE BASE HEAD` 把提交+stat+`git diff -U10` 打包成唯一文件;diff **永远以文件交给评审者**,不进控制器上下文;评审者输入=同一 brief 文件 + 报告文件 + review package + 绑定该任务的 Global Constraints。评审者是**任务级闸门**,绝不小看。
- **步骤 4 fix 循环**:每任务最多 5 轮。1-3 轮 **resume 原实现者**;4-5 轮派新实现者、更强模型。Minor findings 直接记台账(永不进循环);每条 finding 逐条裁决 ADDRESSED/NOT ADDRESSED。**控制器绝不亲自修 finding**。
- **步骤 5 完成任务**:评审干净或有裁决时,记 `Task N: complete (commits ..., review clean)`;有未修/未裁决的 open Critical/Important 时**绝不前进到下一任务**。

**连续执行与停因(Rulings, not stalls)**:执行计划不停。只有四种停因:① 不可逆/破坏性操作;② 安全敏感操作;③ 规范要求先问的副作用(merge、push 共享分支、publish);④ 计划坏到每条前路都是猜。其余冲突/歧义——**自己裁决(Ruling)**:`Ruling: <决定> — <为什么> — <错了的代价>`,记入台账继续。

**终审(Final Review)**:用最强模型,派发 `requesting-code-review` 的 `code-reviewer.md`;终审有 findings → **派一个** fix subagent(绝不 per-finding 各派一个)→ 恰好一次 scoped re-review;没有第二次 fix 波。

**收尾**:删除任何东西前,收集台账中所有 `Ruling:` 行到最终消息的 **"Rulings I made"** 列表(穷尽——台账有裁决列表必须有);终审干净后删除工作区(git 历史是记录);调用 `finishing-a-development-branch`。

**配套脚本**:
- `sdd-workspace`:打印计划工作区路径(自动 mkdir + git-ignore)。放在 working tree 而非 `.git/` 下,因为 harness 把 `.git/` 视为保护路径。
- `task-brief`:用 awk 提取 `### Task N` 标题到其后的全文为唯一 brief 文件。
- `review-package`:写提交列表 + `git diff --stat` + `git diff -U10` 到唯一文件。

**设计要点**:这是把"多人团队协作流程"(spec → 任务 → 实现 → 评审 → 修复 → 终审)搬进单 agent 会话的工程化产物;用文件传递保护上下文、用台账对抗压缩、用 Rulings 解放人类、用模型路由控制成本。

---

### 4.5 executing-plans —— 内联执行器(SDD 轻量替代)

| 项目 | 内容 |
|---|---|
| **名称** | `executing-plans` |
| **description** | "Use when you have a written implementation plan to execute in a separate session with review checkpoints" |
| **文件** | 仅 `SKILL.md`(64 行,极简) |
| **定位** | 在同一会话内联执行书面实现计划(带评审检查点),是 SDD 的轻量替代 |

**核心工作流**:
1. **Load and Review Plan**:用 using-git-worktrees 建立隔离工作区 → 读计划 → 批判性评审 → 有担忧先提出 → 建 todos 开始
2. **Execute Tasks**:逐任务标记 in_progress → 严格按 bite-sized 步骤执行 → 跑验证 → 标记完成
3. **Complete Development**:完成后强制调用 finishing-a-development-branch

**设计要点**:极简是因为它的定位是**无 subagent 时的降级路径**(Harness 无子代理能力时使用)。它把"停止并询问"当作一等公民,与 SDD 的"Rulings 不停摆"形成对照——SDD 是连续执行,executing-plans 是检查点制。明确告知用户:有 subagent 访问时应改用 SDD。

---

### 4.6 test-driven-development(TDD)—— 测试驱动开发

| 项目 | 内容 |
|---|---|
| **名称** | `test-driven-development` |
| **description** | "Use when implementing any feature or bugfix, before writing implementation code" |
| **文件** | `SKILL.md`(320 行)+ `writing-good-tests.md`(198 行,前身是 testing-anti-patterns.md) |
| **定位** | 先写测试、看它失败、写最小代码让它通过;核心:「没看过测试失败,就不知道它测的是对的东西」 |

**核心:Red-Green-Refactor 循环**:

- **Iron Law**:`NO PRODUCTION CODE WITHOUT A FAILING TEST FIRST`。先写代码?**删除重来**。无例外:不留作参考、不边写测试边"改编"、不看它;删就是删。
- **RED — 写失败测试**:一个最小测试,展示应该发生什么。要求:单一行为、清晰命名、真实代码(无 mock 除非不可避免)。
- **Verify RED(强制)**:跑测试确认它**失败**(不是 error)、失败消息符合预期、因功能缺失而败。测试通过=你在测既有行为;测试 error=修 error 重跑。
- **GREEN — 最小代码**:写恰好通过测试的最简代码,不加特性/不重构/"改进"。
- **Verify GREEN(强制)**:确认测试通过、其他测试仍过、输出干净。
- **REFACTOR — 清理**:仅在 green 后:去重、改名、提取 helper;保持绿;不加行为。
- **Repeat**:下一个失败测试对应下一特性。

**配套文件 writing-good-tests.md(重点)**:两大原则——① **每个测试点名它抓的破坏**(写测试体前回答:什么生产改动会让它失败);② **每个测试练习真实之物**(mock 不挣断言)。含 **Mutation Check(变异检查)**:完成前在脑中变异生产代码,每个现实变异至少一个测试失败。

**设计要点**:纪律型技能最典型的样本——Iron Law、无例外列举、Common Rationalizations 表(10+ 行)、Red Flags 列表。它是所有实现任务的底层方法论,被 writing-plans、SDD implementer、systematic-debugging(Phase 4)、writing-skills 引用。

---

### 4.7 systematic-debugging —— 系统化调试

| 项目 | 内容 |
|---|---|
| **名称** | `systematic-debugging` |
| **description** | "Use when encountering any bug, test failure, or unexpected behavior, before proposing fixes" |
| **文件** | `SKILL.md`(283 行)+ `root-cause-tracing.md`、`defense-in-depth.md`、`condition-based-waiting.md` + `find-polluter.sh` + `CREATION-LOG.md` + 4 个测试文件 |
| **定位** | 遇到任何 bug/测试失败/意外行为时,先找根因再修;症状修复=失败 |

**Iron Law**:`NO FIXES WITHOUT ROOT CAUSE INVESTIGATION FIRST` —— 没完成 Phase 1 不能提修复。

**四阶段流程**:
- **Phase 1 根因调查**:① 仔细读错误信息(栈完整、记行号/路径/错误码)② 一致复现(不可复现→收集数据不猜)③ 查最近变更(git diff、新依赖、配置)④ 多组件系统对每个组件边界加诊断插桩 ⑤ 追踪数据流(坏值从哪来,**在源头修而非症状**)
- **Phase 2 模式分析**:① 找同类工作代码 ② **完整读参考实现**(不 skim)③ 列全工作与坏的差异(每条差异,别假设"这不重要")④ 理解依赖
- **Phase 3 假设与测试**:① 单一假设(明确写下"我认为 X 是根因因为 Y")② 最小测试(一次一个变量)③ 验证再继续(有效→Phase 4;无效→新假设,**不要叠加修复**)④ 不知道就说"我不懂 X",不装懂
- **Phase 4 实现**:① 先建失败测试(最简复现,用 TDD 技能)② 实现单一修复(无"顺手"改进)③ 验证修复(用 verification-before-completion)④ 修复无效→STOP,数已试修复数;<3 回 Phase 1;**≥3 停下质疑架构**——每修露出新问题、需要大规模重构、制造新症状,意味着模式根本错误而非假设失败

**配套文件**:
- **root-cause-tracing.md**:栈深处 bug 的完整回溯技术(从症状往上追到原始触发点,在源头修;用 `console.error` 而非 logger 打插桩日志)
- **defense-in-depth.md**:修根因后在数据经过的每一层加验证(入口验证/业务逻辑验证/环境守卫/调试插桩),使 bug 结构上不可能
- **condition-based-waiting.md** + **example.ts**:修 flaky 测试——**等待真实条件而非猜测时长**;通用 `waitFor(condition, description, timeoutMs=5000)`
- **find-polluter.sh**:二分法找哪个测试产生多余文件/状态
- **4 个测试文件**:技能自测产物——`test-academic.md`(知识问答)+ `test-pressure-1/2/3.md`(时间压力/沉没成本/权威与社交压力场景),展示压力场景写法

**设计要点**:典型的纪律型技能——铁律 + Red Flags 反合理化表 + Rationalization 表 + 压力测试文件;"连字母都违反=连精神都违反"是标准 bulletproofing 手法。

---

### 4.8 verification-before-completion —— 完成前验证

| 项目 | 内容 |
|---|---|
| **名称** | `verification-before-completion` |
| **description** | "Use when about to claim work is complete, fixed, or passing, before committing or creating PRs - requires running verification commands and confirming output before making any success claims; evidence before assertions always" |
| **文件** | 仅 `SKILL.md`(120 行) |
| **定位** | 宣称任何"完成/修复/通过"之前必须先跑验证命令并确认输出;证据永远先于断言 |

**Iron Law**:`NO COMPLETION CLAIMS WITHOUT FRESH VERIFICATION EVIDENCE` —— 本条消息没跑过验证命令,就不能声称通过。

**Gate Function(闸门函数)**:① 识别:什么命令能证明这个断言?② 运行:完整命令 ③ 读:完整输出、查退出码、数失败 ④ 验证:输出是否确认断言 ⑤ 然后才断言。跳过任一步=撒谎不是验证。

**Common Failures 表**:每类断言要求的证据(测试通过←测试命令输出 0 失败;"agent 完成"←VCS diff 显示变更,不充分=agent 报告"成功")。

**Key Patterns**:
- 回归测试:写→跑过→**revert fix→必须失败→还原→过**(证明测试真的在测那个修复)
- agent 委托:agent 报成功→查 VCS diff→验证变更→不信任报告

**设计要点**:最短小但最常被违反的技能;核心是反"口头声称成功"的行为纠正。被 SDD 任务评审、finishing-a-development-branch、systematic-debugging 引用。

---

### 4.9 requesting-code-review / receiving-code-review —— 评审闭环

#### requesting-code-review(请求评审)

| 项目 | 内容 |
|---|---|
| **名称** | `requesting-code-review` |
| **description** | "Use when completing tasks, implementing major features, or before merging to verify work meets requirements" |
| **文件** | `SKILL.md`(95 行)+ `code-reviewer.md`(181 行评审者模板) |
| **定位** | 派发 code reviewer subagent 在问题级联前拦截;核心:早评审、勤评审 |

**核心工作流**:
1. 获取 git SHAs(`BASE_SHA`/`HEAD_SHA`)
2. 派 `general-purpose` subagent,填 `code-reviewer.md` 模板占位符
3. 处理反馈:Critical 立即修;Important 继续前修;Minor 记下;评审错了就带理由反驳

**配套文件 code-reviewer.md**:角色=资深代码评审者;输入 DESCRIPTION/PLAN_OR_REQUIREMENTS/BASE/HEAD;**只读评审**(不 mutate working tree;如需其他 revision 用 `git worktree add /tmp/review-[SHA]`,绝不移动 HEAD);**不派 subagent**;检查:计划对齐/代码质量/架构/测试/生产就绪;输出 Strengths、Issues(Critical/Important/Minor,每条带 file:line)、Assessment。

#### receiving-code-review(接收评审)

| 项目 | 内容 |
|---|---|
| **名称** | `receiving-code-review` |
| **description** | "Use when receiving code review feedback, before implementing suggestions, especially if feedback seems unclear or technically questionable - requires technical rigor and verification, not performative agreement or blind implementation" |
| **文件** | 仅 `SKILL.md`(205 行) |
| **定位** | 收到评审反馈时的正确处理方式——技术验证而非情绪表演;先验证再实现、先问再假设 |

**响应模式**:READ(完整读)→ UNDERSTAND(复述)→ VERIFY(对照代码库)→ EVALUATE(技术上成立吗)→ RESPOND(技术确认或有理由反驳)→ IMPLEMENT(一次一项、逐项测试)。

**Forbidden Responses(禁止)**:绝不说"You're absolutely right!"(违反指令文件)、"Great point!"、"Thanks for catching that!"。**改为**:复述技术需求、问澄清问题、带技术理由反驳、直接行动。

**处理不清楚的反馈**:**任何一条不清楚→STOP,什么都不实现,先问清**。部分理解=错误实现。

**外部评审者来源处理**:实现前 5 项检查(对本代码库技术上对吗?破坏既有功能吗?当前实现有原因吗?全平台/版本可行吗?评审者懂完整上下文吗?)。**YAGNI 检查**:评审者建议"properly implement"→grep 代码库实际使用;未用→"这端点没被调用,删掉(YAGNI)?"。

**设计要点**:罕见地**直接禁止讨好性语言**("You're absolutely right!" 被列为显式指令违规)——来自真实会话观察:模型倾向于表演性同意,导致盲目实现错误建议。

---

### 4.10 dispatching-parallel-agents —— 派发并行代理

| 项目 | 内容 |
|---|---|
| **名称** | `dispatching-parallel-agents` |
| **description** | "Use when facing 2+ independent tasks that can be worked on without shared state or sequential dependencies" |
| **文件** | 仅 `SKILL.md`(167 行) |
| **定位** | 面对 2+ 个无共享状态/无顺序依赖的独立任务时,每个问题域派一个 agent 并行处理 |

**触发时机**:多个独立失败(不同测试文件、不同子系统、不同 bug)、每个问题可脱离其他上下文理解、调查间无共享状态。

**何时不用**:失败相关(修一个可能修好其他)、需理解全系统状态、agent 会互相干扰、探索性调试(还不知道坏在哪)、共享状态。

**核心工作流**:识别独立域 → 创建聚焦 agent 任务 → **同一响应里发所有 dispatch**(一个响应多个 dispatch=并行;一个响应一个=顺序)→ 评审与整合 → 验证(查冲突、跑全套件、抽查)。

**Agent Prompt 结构**:聚焦(一个清晰问题域)、自包含(理解问题所需全部上下文)、输出明确(应返回什么)。

**Common Mistakes**:太宽("Fix all the tests"→agent 迷路)、无上下文("Fix the race condition"→不知道在哪)、无约束(可能重构一切)、输出含糊("Fix it"→不知道改了什么)。

**设计要点**:核心洞察是**隔离上下文**——每个 agent 绝不继承你的会话历史,你精确构造它所需的一切,同时保住自己的上下文用于协调。与 SDD 共享同一哲学。

---

### 4.11 using-git-worktrees —— 使用 git worktrees 隔离开发

| 项目 | 内容 |
|---|---|
| **名称** | `using-git-worktrees` |
| **description** | "Use when starting feature work that needs isolation from current workspace or before executing implementation plans - ensures an isolated workspace exists via native tools or git worktree fallback" |
| **文件** | 仅 `SKILL.md`(167 行) |
| **定位** | 确保工作在隔离工作区进行;优先平台原生 worktree 工具,无原生工具才回退手动 git worktrees;绝不与 harness 作对 |

**核心工作流**:
- **Step 0 检测既有隔离**:`GIT_DIR=$(git rev-parse --git-dir)`、`GIT_COMMON=$(git rev-parse --git-common-dir)`。**子模块守卫**:`GIT_DIR != GIT_COMMON` 在子模块里也为真——先用 `--show-superproject-working-tree` 排除。已在 linked worktree 则跳过创建。
- **Step 1 创建隔离工作区**:① 优先**原生工具**(EnterWorktree/WorktreeCreate 等——自动处理目录/分支/清理;用 `git worktree add` 会制造 harness 看不见的 phantom state,是 #1 错误)② 无原生工具才用 git 回退(目录选 `.worktrees/`,创建前必须验证被 git-ignore)。
- **Step 2 项目设置**:自动跑依赖安装(npm install / cargo build / pip install 等)。
- **Step 3 验证干净基线**:跑测试确认工作区干净开始。

**设计要点**(来自 worktree-rototill 设计文档):核心是"**检测状态而非检测平台**"(用 git 原语而非嗅探环境变量)、"**声明式意图,规定式回退**"(有原生工具时让位)、"**基于来源的所有权**"(谁创建谁清理)。TDD 验证推动重设计:抽象表述在 Claude Code 上 2/6 通过,显式列出工具名后提升到 50/50——这是"先写设计,再用行为测试验证设计"的教科书案例。

---

### 4.12 finishing-a-development-branch —— 完成开发分支

| 项目 | 内容 |
|---|---|
| **名称** | `finishing-a-development-branch` |
| **description** | "Use when implementation is complete, all tests pass, and you need to decide how to integrate the work" |
| **文件** | 仅 `SKILL.md`(225 行) |
| **定位** | 实现完成、测试通过后,决定如何整合工作:验证测试 → 检测环境 → 呈现选项 → 执行选择 → 清理 |

**核心工作流**:
1. **Verify Tests**:跑完整测试套件。失败→报告并停;菜单只在绿套件后出现。
2. **Detect Environment**:捕获 `GIT_DIR`/`GIT_COMMON`/`WORKTREE_PATH`。决定菜单与清理方式:普通仓库→标准 3 选项;named-branch worktree→3 选项+来源性清理;detached HEAD→**精简 2 选项(无 merge)**。
3. **Determine Base Branch**:基本分支通常是 fork 来源;不知道就问;**合入前确认**——合错基底难撤销。
4. **Present Options**:3 选项——① 本地 merge 回 base-branch ② push 并创建 Pull Request ③ 保留分支。**菜单必须原样呈现**,等人类回答,**整合决定是他们的**。丢弃工作只发生在人类明确要求时。
5. **Execute Choice**:
   - Option 1 本地合入:切回主仓库根 → checkout base → pull → merge feature → 合并结果上再测 → 清理 worktree → `git branch -d`
   - Option 2 push + PR:`git push -u origin <feature-branch>` → 用 forge 工具建 PR → 报告 URL。**保留 worktree**(人类会针对 PR 反馈迭代)
   - Option 3 保留:报告保留。
   - **人类要求丢弃**:仅当明确请求且**确认**——呈现将永久删除:分支、提交列表、worktree 路径,要求**输入 `discard` 确认**。
6. **Cleanup Workspace**:`.worktrees/` 下的归 Superpowers 清理(`git worktree remove` + prune);**移除被拒**(modified/untracked)→ 绝不自行 `--force`,展示给人类并问;其余归宿主环境,保留原地。

**设计要点**:把"整合决策"严格划给人类(呈现菜单并等);"丢弃"有最严确认门(输入 discard);环境检测决定菜单形态与清理所有权(来源性清理避免误删宿主的工作区)。v6.2.0 起不再主动提供"丢弃工作"选项(防止推荐摧毁已完成的工作)。

---

### 4.13 writing-skills —— 编写技能的技能(元技能)

| 项目 | 内容 |
|---|---|
| **名称** | `writing-skills` |
| **description** | "Use when creating new skills, editing existing skills, or verifying skills work before deployment" |
| **文件** | `SKILL.md`(679 行)+ `testing-skills-with-subagents.md`(384 行)+ `persuasion-principles.md` + `anthropic-best-practices.md`(1150 行)+ `graphviz-conventions.dot` + `render-graphs.js` + `examples/CLAUDE_MD_TESTING.md` |
| **定位** | 编写/编辑/验证新技能的流程;「编写技能就是把 TDD 应用于流程文档」 |

**核心理念**:把 TDD 的概念映射到技能创作:
- 测试用例 = 带压力的 subagent 场景
- 生产代码 = 技能文档(SKILL.md)
- 测试失败(RED) = 无技能时 agent 违反规则(基线)
- 测试通过(GREEN) = 有技能时 agent 遵守
- **Iron Law**:`NO SKILL WITHOUT A FAILING TEST FIRST` —— "如果你没看过 agent 在没有技能时的失败,就不知道技能教的是不是对的东西"

**Skill Discovery Optimization(SDO)—— 关键设计原则**:
- **Rich Description Field**:description 回答"我现在该读这个技能吗?";**description = 何时用,不是技能做什么**。测试证据:描述总结了工作流→agent 可能只照描述做而不读全文(描述说"code review between tasks"导致 agent 只做一次评审)。**绝不绝不总结技能流程或工作流**。
- **Keyword Coverage**:含 agent 会搜的词——错误消息、症状、同义词、工具名。
- **Token Efficiency**:高频加载的技能每 token 都贵;目标词数:引导 <150 词、高频 <200 词、其他 <500 词。
- **Cross-Referencing**:只用技能名 + `**REQUIRED SUB-SKILL:**` 标记;**不用 `@` 链接**(强制加载烧 200k+ 上下文)。

**Match the Form to the Failure(按失败类型匹配形式)—— 核心设计方法论**:
- 基线失败=「压力下跳过规则」→ 正确形式=**禁令 + 合理化表 + red flags**;错误形式=软性引导("prefer...", "consider...")
- 失败=「遵守了但输出形状错」→ 正确=**正面配方/契约**(陈述输出是什么);错误=禁令列表
- 失败=「遗漏必需元素」→ 正确=**结构性**(模板中 REQUIRED 字段);错误=散文提醒
- **为什么禁令在塑形问题上适得其反**:在竞争性激励下,agent 与"don't X"谈判;头对头措辞测试中禁令臂产生明显更多不想要的内容,甚至比无引导对照还差。

**Bulletproofing 技能抗合理化**:
- **Close Every Loophole Explicitly**:不只说规则,显式禁止具体绕过("Write code before test? Delete it. Start over. No exceptions.")
- **Address "Spirit vs Letter"**:早放基础原则"**Violating the letter of the rules is violating the spirit of the rules.**"
- **Build Rationalization Table**:基线测试抓到的每个借口进表(Excuse | Reality)
- **微测措辞(micro-test wording)**:每次全新上下文样本;必须带 no-guidance 对照组;每变体 5+ 重复;方差是度量

**配套文件**:
- **testing-skills-with-subagents.md**:完整测试方法论;压力场景写作(坏=学术问题 agent 复述技能;好=单压力;最好=多压力组合);Meta-Testing("你读了技能还选 C。技能怎么写才能让 A 是唯一可接受答案?")
- **persuasion-principles.md**:LLM 对人类劝说原则同响应;七大原则(权威/承诺/稀缺/社会认同/团结/互惠/喜好),纪律型技能用 权威+承诺+社会认同,避免 喜好+互惠
- **anthropic-best-practices.md**:Anthropic 官方技能编写最佳实践全文引用;渐进披露、先建评估再写文档
- **render-graphs.js**:从 SKILL.md 提取 DOT 流程图渲染成 SVG

**设计要点**:它是「元技能」——把仓库对技能创作的所有实证研究(压力测试、说服心理学、SDO、形式匹配、微测)固化成流程;是"吃自己的狗粮"最集中的体现。

---

### 4.14 技能编排的完整工作流(idea → 合入分支)

```
① 会话开始:SessionStart hook 注入 using-superpowers → 铁律:任何回应前检查技能
② 用户提出想法("Let's build X")→ brainstorming 被强制触发
   → 分类(spike/bounded/architectural)→ 澄清 → 设计 → 获批
   → (架构级)写 spec 并提交 → 用户审阅 → 唯一出口:调用 writing-plans
③ writing-plans 把 spec 拆成 bite-sized 任务计划
   → Self-review → 向用户呈现执行方式二选一
④ 执行(推荐 SDD):using-git-worktrees 建隔离工作区
   → sdd-workspace 建立计划工作区+ledger → pre-flight 扫描
   → 每任务:task-brief → 派 implementer(TDD 任务内强制)→ 处理报告
     → review-package + task reviewer(Spec ✅ + 质量)→ fix 循环(≤5 轮,scoped re-review)
     → 记 ledger → 下一任务
   (遇到 bug → systematic-debugging 四阶段;多独立失败 → dispatching-parallel-agents 并行)
   (备选:executing-plans 内联分批执行)
⑤ 全部任务完成 → 终审(最强模型,code-reviewer.md)→ 单波 fix + 一次 re-review
   → 汇报 "Rulings I made"(穷尽)→ 删除计划工作区
⑥ finishing-a-development-branch:全量测试 → 环境检测 → 3 选项菜单
   → 合入本地(清理 worktree+删分支)/ push+PR(保留 worktree)/ 保留
⑦ 贯穿全程:verification-before-completion(任何完成断言前)
   + requesting-code-review/receiving-code-review(评审闭环)
   + using-git-worktrees/finishing 的环境检测覆盖所有分支工作
```

**关键设计观察**:
1. **上下文卫生是贯穿主题**:subagent 绝不继承会话历史,精确构造上下文;产物走文件,保护控制器上下文。
2. **评审闸门无处不在**:计划被审、每个任务被审、每个 fix 被复审、分支被终审、人类批准处处是硬闸。
3. **反合理化是设计语言**:Red Flags 表、Rationalization 表、Iron Law 出现在几乎每个纪律型技能里。
4. **人类决策点被严格保护**:brainstorming 的批准、SDD 的四大停因、finishing 的整合菜单与 discard 门;其余由代理用 ledger 化 Ruling 自主推进。
5. **成本/质量杠杆**:SDD 的模型路由、writing-skills 的 token 预算与渐进披露。

---

## 五、完整工作流:从想法到合入分支

上面 4.14 已给出完整流程。这里补充 README 中官方描述的七步基本工作流:

| 步骤 | 技能 | 激活时机 | 作用 |
|---|---|---|---|
| 1 | **brainstorming** | 写代码前 | 通过提问澄清想法、探索备选方案、分节呈现设计、保存设计文档 |
| 2 | **using-git-worktrees** | 设计获批后 | 创建隔离工作区、新分支、运行项目设置、验证干净测试基线 |
| 3 | **writing-plans** | 设计获批后 | 把工作拆成 2-5 分钟的小任务,每个任务有精确文件路径、完整代码、验证步骤 |
| 4 | **subagent-driven-development / executing-plans** | 有计划时 | 每任务派发全新 subagent,两级评审(规格合规 → 代码质量);或分批执行带人工检查点 |
| 5 | **test-driven-development** | 实现中 | 强制 RED-GREEN-REFACTOR:写失败测试、看它失败、写最小代码、看它通过、提交 |
| 6 | **requesting-code-review** | 任务间 | 按计划评审,按严重度报告问题;Critical 阻塞进度 |
| 7 | **finishing-a-development-branch** | 任务完成时 | 验证测试、呈现选项(merge/PR/保留)、清理 worktree |

**注意**:步骤 2-6 是核心引擎;遇到 bug 时插入 systematic-debugging,收到评审反馈时用 receiving-code-review,多个独立失败时用 dispatching-parallel-agents。

---

## 六、Subagents:子代理体系

### 6.1 重要发现:仓库没有注册式的 subagents

**`.claude/`、`.claude-plugin/` 中没有任何 agents 目录或 slash commands 配置。** 子代理**不是通过注册 agent 定义分发**,而是通过技能内的**提示词模板(Prompt Templates)**,由主代理调用各 harness 的原生子代理工具(如 Claude Code 的 `Task` 工具、Kimi 的 `Agent` 工具)动态派发。

这意味着:这个仓库是一个**纯技能库**,不携带命令/代理定义。所有"subagent"行为都是技能指导下的运行时派发。

### 6.2 技能内嵌的 Subagent 提示词模板

虽然不注册 agent,但有多份精心设计的提示词模板,供主代理填充占位符后派发:

| 模板 | 位置 | 角色 |
|---|---|---|
| **implementer-prompt.md** | `skills/subagent-driven-development/` | 实现任务 N;先读 brief;不派 subagent;报告 Status(DONE/DONE_WITH_CONCERNS/BLOCKED/NEEDS_CONTEXT) |
| **task-reviewer-prompt.md** | 同上 | 任务级闸门:先查 spec 合规、再查质量;**Do Not Trust the Report**;每条 finding 带 file:line |
| **re-review-prompt.md** | 同上 | 裁决每条 finding + 查 fix diff,不是全新评审;ADDRESSED/NOT ADDRESSED |
| **code-reviewer.md** | `skills/requesting-code-review/` | 资深代码评审者;只读评审;检查计划对齐/质量/架构/测试/生产就绪 |
| **spec-document-reviewer-prompt.md** | `skills/brainstorming/` | 检查 spec 的完整性、一致性、架构、YAGNI |
| **plan-document-reviewer-prompt.md** | `skills/writing-plans/` | 检查计划的完整性、Spec 对齐、任务分解、可构建性 |

### 6.3 设计原因:为什么不用注册式 subagents

1. **跨 harness 兼容**:每个 harness 的 subagent 机制不同(Claude 有 Task 工具、Codex 有 multi_agent、Kimi 有 Agent 工具);技能只描述动作("dispatch a subagent"),工具映射层翻译成具体 harness 调用。
2. **上下文隔离**:派发的 subagent 绝不继承会话历史,主代理精确构造上下文——这保护了主代理有限的上下文窗口。
3. **动态性**:prompt 模板按任务现场填充(brief 路径、diff 路径、findings 列表),比静态注册的 agent 定义更灵活。

---

## 七、多 Harness 集成机制

### 7.1 支持的 Harness(14 个)

Claude Code、Antigravity、Codex App、Codex CLI、Cursor、Devin CLI、Factory Droid、Gemini CLI、GitHub Copilot CLI、Grok Build CLI、Kimi Code、OpenCode、Pi、Hermes Agent。

### 7.2 架构:同一内容,三层机制

Superpowers 在所有 harness 上是**同一份内容**;变化的是"把内容交付给模型并翻译成该 harness 原生工具"的薄层:

1. **Skills(harness 无关)** — `skills/` 是唯一事实来源,所有 harness 逐字共享。技能只描述**动作**,从不指名具体工具。
2. **Tool mapping(每 harness 独立)** — 把动作词汇翻译成真实工具名,存于 `skills/using-superpowers/references/<harness>-tools.md` 或 bootstrap 注入器内联。
3. **Bootstrap(每 harness 独立)** — 会话开始把完整 `using-superpowers/SKILL.md` 注入上下文。

### 7.3 各 Harness 集成方式对比

| Harness | 集成文件 | 技能注册 | Bootstrap 机制 | 工具映射 |
|---|---|---|---|---|
| **Claude Code** | `.claude-plugin/plugin.json` + `hooks/hooks.json` | 约定自动发现 `skills/` | Shape A:session-start 钩子 → `hookSpecificOutput.additionalContext` | 原生 Skill 工具,无需映射 |
| **Cursor** | `.cursor-plugin/plugin.json` + `hooks/hooks-cursor.json` | 显式 `"skills": "./skills/"` | Shape A:`additional_context` | 无(Claude Code 兼容工具面) |
| **Copilot CLI** | 复用 Claude hook 路径 | 同 Claude Code | Shape A:设 `COPILOT_CLI=1` → 顶层 `additionalContext` | 无 |
| **Codex** | `.codex-plugin/plugin.json` | 显式 `"skills"` + **空 `hooks` 对象**抑制自动发现 | 原生技能发现(无注入) | `references/codex-tools.md`(需 `multi_agent = true`) |
| **Gemini CLI** | `gemini-extension.json` + `GEMINI.md` | 自动发现扩展内 `skills/` | Shape C:`contextFileName: "GEMINI.md"`(两个 `@`-include) | `references/gemini-tools.md` |
| **Kimi Code** | `.kimi-plugin/plugin.json` | 显式 `"skills"` | manifest `sessionStart.skill: "using-superpowers"` | 内联 `skillInstructions` |
| **OpenCode** | `.opencode/plugins/superpowers.js` | `config` 钩子注入 `config.skills.paths` | Shape B:`chat.messages.transform` 插入首条 user 消息 | 内联 `toolMapping` |
| **Pi** | `.pi/extensions/superpowers.ts` | `resources_discover` 返回 skillPaths | Shape B:`context` 事件插入 user 消息 | `piToolMapping()` + `references/pi-tools.md` |
| **Hermes Agent** | `.hermes-plugin/plugin.yaml` + `__init__.py` | `ctx.register_skill(name, Path(...))` | `pre_llm_call` 钩子首轮注入 | `references/hermes-tools.md` |
| **Devin CLI** | `.devin-plugin/plugin.json` | 自动发现同目录 `skills/` | 无注入(最弱形态,surfaced skill index) | 无(Devin system prompt 自带) |
| **Antigravity** | 无本仓库目录 | 直接消费现有插件 | Shape A(agy 运行 SessionStart 钩子) | `references/antigravity-tools.md` |
| **Factory Droid / Grok** | 无 | 消费 Claude Code 插件 / 官方 marketplace | — | — |

### 7.4 移植判定标准(硬性要求)

- **硬性要求:自动会话启动注入** —— 无需人工每次 opt-in。"如果唯一方式需要你的 human partner 每次会话 opt-in,这个 harness **不能**被正确支持"。
- **接受测试**:干净会话中发送 `Let's make a react todo list`,必须**在写任何代码之前自动触发 brainstorming 技能**,并在 PR 中贴完整 transcript。
- **这些不是真集成(会被关闭)**:手动复制技能文件、用 `npx skills` 之类运行时 shim 包装、需要用户每次 opt-in、brainstorming 不能自动触发。
- **零依赖**:移植也不允许加第三方运行时依赖。

---

## 八、Rules 与配置体系

### 8.1 规则文件(Rules Files)

| 文件 | 用途 |
|---|---|
| **CLAUDE.md** | 贡献者指南(同时是 AGENTS.md 的软链目标);给 AI agent 的强制规则 |
| **AGENTS.md** | 软链指向 CLAUDE.md(多 harness 兼容) |
| **GEMINI.md** | Gemini 扩展入口;仅两行 `@`-include(using-superpowers + gemini-tools.md) |

### 8.2 指令优先级(Instruction Priority Hierarchy)

v5.0.0 引入的明确优先级:

1. **用户的显式指令**(CLAUDE.md、AGENTS.md、GEMINI.md、直接请求)—— 最高优先级
2. **Superpowers 技能** —— 覆盖默认系统行为
3. **默认系统提示** —— 最低优先级

如果 CLAUDE.md 说"别用 TDD"而技能说"永远用 TDD",**用户的指令赢**。

### 8.3 Hooks 配置(Claude Code)

`hooks/hooks.json` 只定义一个 hook:

```json
{
  "hooks": {
    "SessionStart": [
      {
        "matcher": "startup|clear|compact",
        "hooks": [
          {
            "type": "command",
            "command": "\"${CLAUDE_PLUGIN_ROOT}/hooks/run-hook.cmd\" session-start",
            "shell": "bash",
            "async": false
          }
        ]
      }
    ]
  }
}
```

关键点:
- matcher `startup|clear|compact`:每次会话/上下文重置都触发(启动、`/clear`、`/compact`)
- `shell: "bash"`:强制 Git Bash 路线(Windows 修复,Claude Code 2.1.81+)
- `async: false`:同步等待 hook 输出

### 8.4 Session-start 脚本的关键设计

- **只发射一个 JSON 字段**:Claude Code 同时读取 `additional_context` 和 `hookSpecificOutput` 且不去重,多发射会双重注入。
- **分支顺序敏感**:Cursor 分支在前(某些 harness 会同时设 `CLAUDE_PLUGIN_ROOT`)。
- **无扩展名脚本**:避免 Claude Code 在 Windows 上对 `.sh` 自动前置 bash 导致二次调用。
- **`run-hook.cmd` 是 polyglot**:一个文件同时是 cmd 批处理和 bash 脚本(用 `CMDBLOCK` heredoc 技巧),Windows 上找 Git Bash,Unix 上直接执行。

### 8.5 版本管理配置

`.version-bump.json` 声明 9 处需同步的版本字段(各 harness plugin.json/yaml + package.json + marketplace.json 嵌套字段),由 `scripts/bump-version.sh` 统一更新,支持 `--check`(检测漂移)和 `--audit`(扫描未声明引用)。

---

## 九、测试与评估体系

### 9.1 两类测试的清晰分工(核心理念)

| 目录 | 测试对象 | 工具 | 是否进 CI |
|---|---|---|---|
| **`tests/`** | 插件的**非 LLM 代码**是否工作(manifest、脚本、服务器、插件加载) | Bash + Node + Python | 手动运行(无 GitHub Actions CI) |
| **`evals/`** | agent 在**真实 LLM 会话**中行为是否正确 | Python(drill harness,驱动真实 tmux 会话) | 否(每个场景 3-30 分钟) |

### 9.2 tests/ 目录结构

```
tests/
├── hermes/                    # Python pytest(Hermes 插件)
├── brainstorm-server/         # Node 测试套件(零依赖服务器)
├── opencode/                  # OpenCode 插件测试
├── claude-code/               # 调用真实 claude CLI 的集成测试
├── explicit-skill-requests/   # 显式点名 skill 的测试(多轮/Haiku)
├── systematic-debugging/      # find-polluter.sh 确定性测试
├── writing-skills/            # render-graphs.js 测试
├── hooks/                     # session-start 输出形状测试
├── version-bump/              # bump-version.sh 测试
├── codex/ codex-plugin-sync/  # Codex 打包/市场清单测试
├── kimi/ devin/ pi/ antigravity/  # 各 harness manifest 测试
└── shell-lint/                # lint-shell.sh 自身测试
```

### 9.3 代表性测试方法

- **`test-session-start.sh`**:用 `env -i` 隔离环境跑真实 hook,内嵌 Node 解析 JSON,验证三种输出形状互不混入。
- **`test-subagent-driven-development.sh`**:连续 9 个 prompt,每个用结构化回答格式约束,断言关键词("self-review"、"skeptical"等)。v6.2 修复了 flake:超时上限 900s、匹配改大小写不敏感。
- **`test-worktree-native-preference.sh`**:RED-GREEN-REFACTOR 验证的范例——RED(无引导用 `git worktree add`)/ GREEN(有引导用 `EnterWorktree`)/ PRESSURE(时间压力)。注释记录「50/50 runs 零失败」。
- **`test-sdd-workspace.sh`**:纯确定性 bash 测试,直接调用 SDD 脚本断言行为。
- **`test-find-polluter.sh`**:用 stub 技术(在 `$PATH` 前放假 `npm`)测试脚本。
- **`test-lint-shell.sh`**:写假 `shellcheck`/`shfmt` 到 fakebin,记录调用参数验证传参正确。

### 9.4 Eval 机制(evals/ + drill)

- **`evals/` 本地不存在**(gitignore 排除)——独立在 [superpowers-evals](https://github.com/prime-radiant-inc/superpowers-evals/) 仓库,克隆进 `evals/` 供本地开发。
- **Drill 工作原理**:Python 技能合规基准测试框架。三个核心组件:
  1. **LLM actor** 作为模拟用户(按 `prompts/actor.md` 扮演)
  2. **LLM verifier** 在结果 transcript 上判定 pass/fail(按 `prompts/verifier.md`)
  3. **引擎驱动真实 tmux 会话**运行 Claude Code / Codex / Gemini CLI
- **运行方式**:
  ```bash
  cd evals
  uv sync --extra dev
  export ANTHROPIC_API_KEY=sk-...
  uv run drill run triggering-test-driven-development -b claude
  ```
- **删除门槛**:bash 测试仅当有 drill 场景逐断言覆盖它时才可删除,且必须由独立 subagent 把关(逐个配对断言)。

### 9.5 工程脚本

| 脚本 | 用途 |
|---|---|
| `scripts/bump-version.sh` | 版本同步 + 漂移检测 + 未声明引用审计 |
| `scripts/lint-shell.sh` | ShellCheck + shell 语法检查(默认只 lint 变更文件) |
| `scripts/package-codex-plugin.sh` | 构建确定性 Codex 分发包(规范化时间戳、字节一致) |
| `scripts/sync-to-codex-plugin.sh` | 镜像到外部 fork 并开 PR(带 include/exclude 列表) |

### 9.6 pre-commit 配置

只针对 evals/ 的 Python 代码(ruff lint/format + ty type check,用 uv 驱动)。shell 脚本质量由 lint-shell.sh + tests/shell-lint/ 守护。

---

## 十、版本演进历史

### 10.1 时间线概览

| 版本 | 日期 | 主题 |
|---|---|---|
| v1.0.0 | 2025-10-09 | 初始发布(Jesse Vincent + Prime Radiant) |
| v2.0.0 | 2025-10-12 | **技能仓库分离**:skills 移到独立仓库,superpowers 变轻量 shim |
| v3.0.x | 2025-10-16 | **技能合并回主仓库**(Anthropic 推出第一方 skills 系统后放弃分仓) |
| v3.x | 2025-10~11 | 行为塑造强化:EXTREMELY-IMPORTANT、Rationalizations 表、命名空间 |
| v4.0.0 | 2025-12-17 | DOT 流程图作为可执行规范;发现 **Description Trap**;两级评审 |
| v5.0.0 | 2026-03-09 | SDD 强制化;视觉同伴;文档审查系统;指令优先级层次 |
| v6.0.0 | 2026-06-16 | SDD 审查流程重写;3 个新 harness;worktree 落项目内;eval 独立 |
| v6.1.x | 2026-06-30 | token 成本压缩;Codex marketplace 支持 |
| v6.2.0 | 2026-07-23 | SDD plan-scoped workspace;全库压缩战役;Windows Git Bash 修复 |
| v6.3.0 | 2026-08-12 | Devin/Hermes/Grok 支持;brainstorming 三路径;SDD 自治增强 |

### 10.2 关键演进决策(用 eval 数据驱动)

- **v4.0 的 Description Trap**:发现技能 description 总结工作流时,agent 只照 description 做而不读全文。修复:description 必须是纯触发条件("Use when X"),绝不含流程细节。
- **v6.2 的 TDD 删节决策**:删除 "Why Order Matters" 段导致 test-first 行为退化(control 8/10 → treatment 5/10),所以每个反驳改为 Rationalization 表行保留——**用 eval 数据决策不删段**。
- **v6.2 的压缩战役**:删除 recap/social proof/benefits-selling 散文,每个承重论点并入 rationalization-table 行;每处删减都用 subagent 探针微测,唯一可测退化的一处被重做。
- **v6.3 的 SDD 自治**:非灾难性冲突由控制器记录裁决并继续(一个真实会话曾被阻塞 9 小时等待人类);用穷尽式 "Rulings I made" 列表保证决策可见可反悔。

### 10.3 总体演进趋势

1. **多 harness 支持持续扩张**(Claude Code → Codex/Cursor/Copilot/Gemini → OpenCode/Kimi/Pi/Antigravity → Devin/Hermes/Grok)
2. **从"行为塑造散文"转向"精简 + rationalization 表格 + 结构化格式"**,一切用 eval 证据驱动
3. **SDD 从"重流程"走向"成本优化 + 自治 + 结构性防错"**
4. **测试基建从 in-tree bash 转向独立 eval harness(drill)**
5. **工程可靠性投入**:确定性构建、Windows 跨平台、版本一致性自动化

---

## 十一、设计哲学:为什么这样设计

### 11.1 核心理念:行为塑造(Behavior-Shaping)而非文档

> **"Skills are not prose — they are code that shapes agent behavior."**(技能不是散文,它们是塑造 agent 行为的代码。)

推论:
- 修改技能正文需要**评估证据**(eval evidence)
- Red Flags 表、rationalization 表、"human partner" 语言等"精心调校的内容"未经证据不得修改
- 不接受为"符合 Anthropic 官方 skill 写作指南"而做的合规性重写(项目有自己的、经实证验证的哲学)

### 11.2 三大核心设计原则

**① 上下文卫生(Context Hygiene)**
- subagent 绝不继承会话历史,主代理精确构造上下文
- 产物(设计、计划、brief、report、diff)走文件传递,保护控制器有限的上下文窗口
- 这是贯穿 SDD、dispatching-parallel-agents、requesting-code-review 的哲学

**② 评审闸门无处不在(Review Gates Everywhere)**
- 计划被审(spec/plan reviewer)、每个任务被审(task reviewer)、每个 fix 被复审(scoped re-review)、分支被终审(final review)、人类批准处处是硬闸
- 评审是"闸门 + 方向盘":既阻塞错误进入,又驱动修复方向

**③ 反合理化(Bulletproofing Against Rationalization)**
- Red Flags 表、Rationalization 表、Iron Law、"字面即精神"出现在几乎每个纪律型技能里
- 这是 writing-skills 用压力测试实证过的、防 agent 绕过的核心手法
- 心理学基础:权威(祈使语气)+ 承诺(要求宣布)+ 社会认同;避免喜好 + 互惠

### 11.3 关键工程洞察

**"检测状态而非检测平台"**(worktree-rototill):用 `GIT_DIR != GIT_COMMON` 等 git 原语判断状态,而非嗅探环境变量识别 harness。零维护成本、跨平台通用。

**"轮数成本 > token 价格"**(SDD 模型路由):最便宜模型常多花 2-3 倍轮数;中档模型是评审者与散文式实现的下限。

**"技能描述 = 何时用,不是做什么"**(SDO):总结工作流的描述创造了 agent 会走的捷径,技能正文变成被跳过的文档。

**"平台中立化"**:随着 harness 增多,项目系统性清除 Claude Code 特定的散文、配置引用、README 排序——"同一内容处处运行"哲学的必然延伸。

**"零依赖"**:vendored node_modules(714 个文件)换成单一 `server.js`(250 行,手写 WebSocket 协议);动机是供应链风险。测试仍可用 ws 作为 test-only 依赖。

**"吃自己的狗粮"**:这个仓库本身就在用 superpowers 方法论开发自己——设计文档、实现计划、SDD 执行、评估证据都是它自己工作流的产物。

### 11.4 术语表

| 术语 | 含义 |
|---|---|
| **bootstrap** | 每个会话开始时注入的引导技能(包 `<EXTREMELY_IMPORTANT>` 标签);"The bootstrap is the entire integration" |
| **human partner** | 项目对使用者的刻意称呼,强调合作关系;受保护的行为塑造语言 |
| **harness** | 承载 agent 的 IDE/CLI/runner |
| **tool mapping** | 把技能动作词汇翻译成特定 harness 真实工具名的参考文件 |
| **SDD** | subagent-driven development:每任务派全新 implementer + 两级评审 + 断路器 |
| **RED-GREEN-REFACTOR** | TDD 循环;测试前写的代码会被删 |
| **ledger** | SDD 进度账本(`.superpowers/sdd/<plan>/progress.md`),压缩后存活的持久记忆 |
| **drill** | 行为 eval 工具:驱动真实 tmux 会话,LLM verifier 判定技能合规 |
| **rationalization table** | `\| Excuse \| Reality \|` 反合理化表,决策时刻反驳模型借口 |
| **spike / bounded / architectural** | brainstorming 的三路径分类器;"棘轮单向" |
| **Shape A/B/C** | 移植的三种结构形态 |
| **YAGNI / DRY** | You Aren't Gonna Need It / Don't Repeat Yourself |

---

## 十二、贡献指南

### 12.1 现实:94% PR 拒绝率

CLAUDE.md 直言这个仓库有 94% 的 PR 拒绝率,几乎每个被拒 PR 来自没读指南的 agent。维护者会在几小时内关闭"slop PR",公开评论如"This pull request is slop that's made of lies."

### 12.2 提交前 6 项强制检查

1. 读完整 PR 模板并填写每个部分(真实、具体回答)
2. 搜索已有 PR(开 + 关),发现重复就停止
3. 验证这是真实问题(问:什么坏了?什么失败了?用户体验是什么?)
4. 确认改动属于 core(领域特定/工具特定/第三方推广 → 独立插件)
5. **自我披露**(模型、harness、harness 版本、所有已装插件)
6. **向 human partner 展示完整 diff 并获明确批准**

### 12.3 绝不接受的内容

- 第三方依赖(零依赖设计)
- "合规性"技能重写(无 eval 证据)
- 项目特定/个人配置
- 批量/spray-and-pray PR
- 投机性/理论性修复(无真实问题陈述)
- 领域特定技能
- fork 特定改动
- 捏造内容
- 捆绑无关改动

### 12.4 技能改动的硬要求

- 用 `superpowers:writing-skills` 开发和测试
- 进行对抗性压力测试(多会话)
- 在 PR 中展示 before/after eval 结果
- 不得无证据修改精心调校内容(Red Flags 表、rationalization 表、"human partner" 语言)

### 12.5 新 Harness 支持的要求

必须包含完整会话 transcript 证明集成端到端工作。验收测试:干净会话发送 `Let's make a react todo list`,必须自动触发 brainstorming。**"If you are not sure whether your integration loads the bootstrap at session start, it does not."**

### 12.6 开发分支策略

**所有 PR 必须指向 `dev` 分支,不是 `main`**。`main` 是发布分支;活跃工作在 `dev` 上先落地。

---

## 附:核心文件索引

| 文件 | 作用 |
|---|---|
| `skills/using-superpowers/SKILL.md` | 引导技能(整个系统的总开关) |
| `skills/brainstorming/SKILL.md` | 设计流程(三路径 + HARD-GATE) |
| `skills/subagent-driven-development/SKILL.md` | SDD(最复杂的执行器) |
| `skills/writing-skills/SKILL.md` | 元技能(如何编写技能) |
| `hooks/hooks.json` + `hooks/session-start` | 会话引导钩子 |
| `docs/porting-to-a-new-harness.md` | 移植权威指南(三种形态) |
| `docs/superpowers/specs/` + `plans/` | 设计文档 + 实现计划(自用工作流产物) |
| `.github/PULL_REQUEST_TEMPLATE.md` | PR 模板(贡献门槛) |
| `RELEASE-NOTES.md` | 版本发布说明(1400 行) |
| `.version-bump.json` + `scripts/bump-version.sh` | 版本同步机制 |
| `tests/` | 插件基础设施测试 |
| `superpowers-evals`(外部) | 行为评估 harness(drill) |



