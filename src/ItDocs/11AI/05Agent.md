# 1. Claude Code 关键概念分析

> 基于 Anthropic 官方文档（code.claude.com/docs）整理，更新至 2026 年 7 月

---

## 一、概念总览

| 概念 | 一句话定义 |
| --- | --- |
| **Tool** | Claude 可调用的原子操作（读/写/执行/搜索等），是 Claude 具备行动能力的基础 |
| **Command** | 会话中通过 `/` 触发的操作，包括内置硬编码命令和 Skills |
| **Skill** | `SKILL.md` 文件中的 prompt 剧本，告诉 Claude 如何完成特定任务 |
| **Memory** | 跨会话持久化知识的机制：CLAUDE.md（你写的）+ Auto memory（Claude 自己写的） |
| **Subagent** | 独立上下文窗口中运行的专用 AI 助手，有自己的 prompt 和工具集 |
| **MCP** | 开放标准协议，用于连接外部数据源和服务，给 Claude 提供新工具 |
| **Hook** | 生命周期特定点自动执行的用户定义处理器（确定性的自动化） |
| **Loop** | 会话内的定时重复执行机制（`/loop`） |
| **Rules** | `.claude/rules/*.md` 中的模块化指令文件，支持路径作用域 |
| **Agent Teams** | 多个独立 Claude Code 会话协同工作，Agent 间可互相通信 |
| **Plugin** | Skill + Subagent + Hook + MCP 的打包单元，可分发的扩展包 |
| **Goal** | 设置完成条件，Claude 自动持续工作直到条件满足 |
| **Dynamic Workflows** | JavaScript 脚本编排大量 Subagent 在后台执行 |
| **Routines** | 云端长期运行的自动化任务（定式/事件/API 触发） |
| **Artifact** | 从会话发布的实时交互网页，可分享或公开 |

---

## 二、各概念独立分析

---

### 1. Tool（工具）

| 维度 | 说明 |
| --- | --- |
| **是什么** | Claude 可以调用的**原子操作**。读文件、写文件、执行命令、搜索代码、发请求等。总共约 35 个内置工具 |
| **为什么需要** | Tools 是 Claude **具备行动能力的基础**。没有 tools，Claude 只能输出文本建议（聊天 AI）；有了 tools，它可以直接修改代码、运行测试、部署 |
| **谁触发** | **Claude 自己**（在 agentic loop 中自主决定调用哪个 tool） |
| **怎么触发** | Claude 在推理时决定 → 生成 tool call → 系统执行 → 结果返回给 Claude 继续推理 |
| **什么时候触发** | Agentic loop 中的"take action"阶段，Claude 认为需要执行某个操作时 |
| **什么场景使用** | 一切涉及实际操作的时候：读代码、改文件、git 操作、搜索、web 请求、创建 subagent 等 |
| **如何使用** | 用户不直接调用 tools。用户通过 prompt 表达需求，Claude 自行选择合适的 tool |
| **典型举例** | `Read`、`Edit`、`Write`、`Bash`、`Grep`、`Glob`、`WebSearch`、`Agent`（启动 subagent）、`Workflow`（启动 workflow） |

**关键细节**：部分 tool 需要权限审批（如 `Edit`、`Write`、`Bash`），部分不需要（如 `Read`、`Grep`、`Glob`）。可在 settings 中用 `allow`/`deny`/`ask` 规则控制每个 tool 的行为。

**完整 Tool 列表**：

| Tool | 用途 | 需要权限 |
| --- | --- | --- |
| `Read` | 读取文件内容 | ❌ |
| `Edit` | 精确编辑文件 | ✅ |
| `Write` | 创建新文件 | ✅ |
| `Bash` | 执行 Shell 命令 | ✅（部分只读命令免审批） |
| `Grep` | 搜索文件内容 | ❌ |
| `Glob` | 按模式查找文件 | ❌ |
| `WebSearch` | 网页搜索 | ✅ |
| `WebFetch` | 获取 URL 内容 | ✅ |
| `Agent` | 启动 Subagent | ❌ |
| `Workflow` | 运行 Dynamic Workflow | ✅ |
| `Artifact` | 发布交互网页 | ✅ |
| `Skill` | 执行 Skill | ✅ |
| `AskUserQuestion` | 向用户提问 | ❌ |
| `EnterPlanMode` | 进入计划模式 | ❌ |
| `ExitPlanMode` | 提交计划退出计划模式 | ✅ |
| `EnterWorktree` | 创建/进入 git worktree | ✅ |
| `ExitWorktree` | 退出 worktree | ❌ |
| `NotebookEdit` | 修改 Jupyter notebook | ✅ |
| `PowerShell` | 执行 PowerShell 命令 | ✅ |
| `PushNotification` | 发送桌面/手机通知 | ❌ |
| `SendMessage` | 向 Agent team 成员发消息 | ❌ |
| `SendUserFile` | 向用户发送文件 | ❌ |
| `CronCreate` | 创建定时任务 | ❌ |
| `CronDelete` | 删除定时任务 | ❌ |
| `CronList` | 列出定时任务 | ❌ |
| `RemoteTrigger` | 创建/管理 Routines | ❌ |
| `ReportFindings` | 报告代码审查结果 | ❌ |
| `TaskCreate`/`List`/`Get`/`Stop`/`Update` | 任务管理 | ❌ |
| `ToolSearch` | 搜索延迟加载的 MCP 工具 | ❌ |

---

### 2. Command（命令）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 会话中通过 `/` 触发的操作。包括**内置命令**（CLI 中硬编码的固定逻辑）和 **Skills**（prompt 驱动） |
| **为什么需要** | 提供快捷方式来控制会话行为，无需用自然语言描述意图 |
| **谁触发** | **用户**输入 `/xxx` |
| **怎么触发** | 用户在 prompt 开头输入 `/xxx`，可带参数。文本跟在命令名后面成为参数 |
| **什么时候触发** | 用户需要执行特定操作时（切换模型、清空上下文、审查代码等） |
| **什么场景使用** | 会话控制、模型切换、代码审查、上下文管理等 |
| **如何使用** | 直接在对话中输入 `/命令名 [参数]` |

**内置命令 vs Skills（都通过** `/` 触发）对比：

| 对比维度 | 内置命令 | Skill（作为命令使用时） |
| --- | --- | --- |
| 逻辑来源 | CLI 二进制硬编码 | Claude 执行的 prompt 剧本 |
| 行为特征 | 固定、可预测 | 灵活、随上下文变化 |
| 能否自动触发 | ❌ 只能手动 | ✅ 可手动也可自动 |
| 谁能创建 | 仅 Anthropic | 任何人 |
| 举例 | `/clear`, `/model`, `/exit` | `/code-review`, `/debug`, `/batch` |

**完整 Command 分类**：

```
所有 /xxx 入口
├── 内置命令（固定逻辑，硬编码在 CLI）
│   ├── 会话控制: /clear, /compact, /exit, /cd, /resume, /rewind
│   ├── 模型: /model, /effort
│   ├── 上下文: /context, /cost, /usage, /status
│   ├── 配置: /init, /memory, /config, /permissions
│   ├── 工具: /mcp, /agents, /plugins, /hooks
│   └── 其他: /help, /btw, /voice, /radio, /teleport, /goal
│
├── Bundled Skills（prompt 剧本，随 CLI 内置）
│   ├── 代码: /code-review, /security-review, /review
│   ├── 调试: /debug, /doctor, /batch
│   ├── 运行: /run, /verify, /run-skill-generator
│   ├── 循环: /loop
│   └── 其他: /claude-api, /plan
│
├── 用户自定义 Skills（`.claude/skills/<name>/SKILL.md`）
│   └── /deploy, /docker-debug, /summarize-changes 等
│
└── Bundled Workflows（JavaScript 脚本编排）
    └── /deep-research
```

---

### 3. Skill（技能）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 一个 `SKILL.md` 文件，包含 YAML frontmatter + Markdown 指令。是**给 Claude 看的 prompt 剧本**，告诉 Claude 怎样完成特定任务 |
| **为什么需要** | ① 将重复性工作流标准化、可复用、可分享；② 避免每次手动输入相同的多步指令；③ 遵循 [Agent Skills](https://agentskills.io) 开放标准，跨 AI 工具通用；④ Skill 的 body 只在使用时加载，不浪费上下文 |
| **谁触发** | **两种方式**：① **用户**手动 `/skill-name`；② **Claude 自动匹配**（根据 description 字段判断是否与当前任务相关） |
| **怎么触发** | ① 手动输入 `/技能名`；② Claude 在处理用户请求时，自动从 skill 目录加载匹配的 skill |
| **什么时候触发** | ① 用户明确要求时；② Claude 发现当前任务与某个 skill 的 description 匹配时自动加载 |
| **什么场景使用** | 任何有固定流程的重复性工作：代码审查流程、部署步骤、调试规范、项目初始化等 |
| **如何使用** | 创建 skill 文件 → Claude 自动加载或手动 `/name` 调用 |

**Skill 的额外能力**（相比旧 Custom Commands）：

| 能力 | 说明 |
| --- | --- |
| **目录结构** | 可附带 templates/scripts/references 等支持文件 |
| **Frontmatter 控制** | 控制触发方式、调用者、运行模型等 |
| **动态上下文注入** | `!` 命令\`\` 在加载时动态执行并填入输出 |
| **自动匹配触发** | Claude 根据 description 自动判断是否加载 |
| **链式调用** | 最多 6 个串联：`/skill-a /skill-b do XYZ` |
| **Subagent 执行** | 设置 `subagent: true` 在独立上下文中运行 |
| **Plugin 命名空间** | `plugin-name:skill-name`，避免冲突 |
| **可打包进 Plugin** | 通过 marketplace 分发 |

**Skill Frontmatter 字段**：

```yaml
---
description: 当用户遇到 Docker 问题时使用     # 描述，用于自动匹配
invoke: manual                                 # optional/manual，控制是否允许Claude自动触发
subagent: true                                 # 在 subagent 中运行
model: opus                                    # 指定模型
disable-model-invocation: true                 # 禁止 Claude 自动加载
---
```

**Skill 存储位置**：

| 位置 | 路径 | 适用范围 |
| --- | --- | --- |
| 组织级 | Managed Settings | 整个组织 |
| 用户级 | `~/.claude/skills/<name>/SKILL.md` | 所有项目 |
| 项目级 | `.claude/skills/<name>/SKILL.md` | 当前项目 |
| 插件级 | `<plugin>/skills/<name>/SKILL.md` | 启用插件的场景 |
| 嵌套（monorepo） | `<pkg>/.claude/skills/<name>/SKILL.md` | 子目录 |

---

### 4. Memory（记忆系统）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 跨会话持久化知识的机制，包含两部分：**CLAUDE.md**（你写的指令）+ **Auto memory**（Claude 自己写的笔记） |
| **为什么需要** | 每个会话从空白上下文开始，必须有一种方式把项目知识、编码规范、你的偏好带到新会话中 |
| **谁触发** | **系统自动加载**，无需用户或 Claude 主动触发 |
| **怎么触发** | 会话启动时，Claude Code 自动扫描目录树加载 CLAUDE.md，并从 `~/.claude/projects/<repo>/memory/` 加载 auto memory |
| **什么时候触发** | ① 会话启动时自动加载；② Compaction 后重新加载；③ Claude 在会话中主动读写 auto memory |
| **什么场景使用** | 项目规范（缩进风格、命名规则）、架构说明、常用命令、环境配置 |

**CLAUDE.md vs Auto memory 对比**：

| 对比维度 | CLAUDE.md | Auto memory |
| --- | --- | --- |
| **作者** | **你**（人类） | **Claude**（AI 自己） |
| **存储位置** | 项目根 `./CLAUDE.md` 或用户级 `~/.claude/CLAUDE.md` | `~/.claude/projects/<repo>/memory/` |
| **内容范围** | 项目约定、架构、命令 | Claude 从对话中积累的关于项目的知识 |
| **更新方式** | 你手动编辑（或 `/memory` 命令） | Claude 在对话中自动写入 |
| **压缩后** | ✅ 保留，从磁盘重新加载 | ✅ 保留，从磁盘重新加载 |
| **大小限制** | 无限制（但精简效果更好） | 前 200 行或 25KB 加载，超出部分按需读取 |
| **加载顺序** | 从文件系统根到工作目录 | 启动时加载 index 文件 |

**Memory 加载层次**（目录树遍历）：

```
从当前工作目录向上扫描每个目录：
  foo/bar/ 中执行 claude
  → 加载 /foo/CLAUDE.md（如果有）
  → 加载 /foo/CLAUDE.local.md
  → 加载 /foo/bar/CLAUDE.md
  → 加载 /foo/bar/CLAUDE.local.md
  也扫描子目录中的 CLAUDE.md，在读写文件时按需加载
```

---

### 5. Subagent（子代理）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 在**独立上下文窗口**中运行的专用 AI 助手。有自己的 system prompt、工具权限、上下文窗口 |
| **为什么需要** | ① **保护主会话上下文**：把大量搜索/日志/文件内容隔离在子会话中，只返回摘要；② **并行工作**：多个 subagent 可同时运行（v2.1.198+ 默认后台运行）；③ **专业化**：为不同任务定制不同 prompt 和工具集 |
| **谁触发** | **Claude 主代理**（在 agentic loop 中通过 `Agent` tool 触发） |
| **怎么触发** | Claude 自主决定需要 subagent → 调用 `Agent` tool → 传入任务描述和配置 → subagent 独立执行 → 返回结果摘要 |
| **什么时候触发** | Claude 认为某个子任务适合隔离处理时：深度研究、大规模重构、独立验证等 |
| **什么场景使用** | 代码库范围搜索、独立验证测试结果、多角度研究、隔离高风险操作 |
| **如何使用** | ① 使用内置 subagent（Explore、Plan、General-purpose）；② 自定义 subagent（`.claude/agents/<name>.md`）；③ 在 skill 中设置 `subagent: true` |

**内置 Subagent**：

| 名称 | 用途 |
| --- | --- |
| **Explore** | 探索代码库、搜索模式、理解项目结构 |
| **Plan** | 在计划模式下设计方案 |
| **General-purpose** | 通用任务委托 |

**自定义 Subagent 示例**：

```markdown
# .claude/agents/security-reviewer.md
---
name: security-reviewer
description: 专门进行安全代码审查
model: opus
tools: [Read, Bash]
---

你是一名资深安全工程师。审查代码时关注：
- SQL 注入、XSS、命令注入
- 认证/授权缺陷
- 密钥硬编码
- 不安全的反序列化
```

---

### 6. MCP（Model Context Protocol）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 一个**开放标准协议**，用于将 AI 工具连接到外部数据源和服务。MCP 服务器给 Claude 提供新的 tool |
| **为什么需要** | Claude 内置工具只能操作文件系统和 shell。要连接数据库、Jira、Slack、GitHub API、浏览器等外部服务，需要 MCP |
| **谁触发** | **Claude 主代理**（在 agentic loop 中把 MCP 提供的工具当作普通 tool 调用） |
| **怎么触发** | MCP 服务器注册新的 tool → Claude 在推理时看到这些 tool → 像调用内置 tool 一样调用它们 |
| **什么时候触发** | Claude 认为需要外部服务时（查 Jira ticket、查数据库、操作 GitHub PR 等） |
| **什么场景使用** | 数据库查询、GitHub 操作、Slack 通知、Jira 管理、浏览器自动化、Kubernetes 操作等 |
| **如何使用** | `claude mcp add <name> -- <command>` 或编辑 `.mcp.json` 配置文件 |

**MCP 作用域**：

| 作用域 | 存储位置 | 适用场景 |
| --- | --- | --- |
| `-s user`（全局） | `~/.claude.json` | 所有项目都可用 |
| `-s local`（个人） | `.claude/settings.local.json` | 仅当前项目（gitignored） |
| `-s project`（团队） | `.claude/settings.json` | 当前项目（git 跟踪） |

**MCP Tool Search**：启动时只加载 MCP 工具名称，Claude 决定用哪个时才加载完整 schema。节省上下文。自动启用。

---

### 7. Hook（钩子）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 在 Claude Code 生命周期特定点**自动执行**的用户定义处理器。支持 Shell 命令、HTTP 端点、MCP tool、LLM prompt、Subagent |
| **为什么需要** | 提供**确定性的自动化**——有些事必须每次发生（比如保存后格式化代码），不能依赖 LLM 自己决定是否要做 |
| **谁触发** | **系统自动触发**（在固定的生命周期点） |
| **怎么触发** | 配置 `.claude/settings.json` 中的 hooks 字段，定义事件 + 匹配器 + 处理器 |
| **什么时候触发** | 在 8 个预定义的生命周期事件点 |
| **什么场景使用** | 保存后自动格式化、危险命令拦截、完成后发通知、会话启动加载环境 |
| **如何使用** | 在 settings.json 中配置 |

**8 个 Hook 事件**：

| Hook 事件 | 触发时机 | 典型用途 |
| --- | --- | --- |
| `UserPromptSubmit` | 用户发送 prompt 之前 | 输入校验、日志 |
| `PreToolUse` | 工具执行之前 | 安全门禁，拦截危险命令（exit 2 = 阻止） |
| `PostToolUse` | 工具执行之后 | 自动格式化、运行 linter |
| `Notification` | 权限请求或输入等待时 | 桌面通知、提醒 |
| `Stop` | Claude 回复完成后 | 完成日志、状态更新 |
| `SubagentStop` | Subagent 完成时 | Agent 编排 |
| `PreCompact` | 上下文压缩之前 | 备份会话记录 |
| `SessionStart` | 会话开始时 | 加载开发上下文 |

**Hook 配置示例**：

```json
{
  "hooks": {
    "PostToolUse": [{
      "matcher": "Write(*.py)",
      "hooks": [{"type": "command", "command": "ruff check --fix $CLAUDE_FILE_PATHS"}]
    }],
    "PreToolUse": [{
      "matcher": "Bash",
      "hooks": [{"type": "command", "command": "if echo \"$CLAUDE_TOOL_INPUT\" | grep -q 'rm -rf'; then echo 'Blocked!' && exit 2; fi"}]
    }],
    "Stop": [{
      "hooks": [{"type": "command", "command": "echo 'Claude finished' >> /tmp/claude.log"}]
    }]
  }
}
```

**Hook vs Skill 本质区别**：

| 维度 | Hook | Skill |
| --- | --- | --- |
| **执行方式** | **确定性执行**（匹配就运行） | **建议性执行**（Claude 决定是否遵循） |
| **谁控制** | **你控制**是否执行 | **Claude 控制**是否执行 |
| **逻辑类型** | Shell 命令 / HTTP / 固定逻辑 | 自然语言指令 |
| **适合什么** | **必须做的事**（安全门禁、格式化） | **参考性流程**（审查步骤、调试流程） |
| **Claude 感知** | Claude 不直接"知道" hook 存在 | Claude 读取并理解 skill 内容 |
| **举例** | "每次写 Python 文件后自动 ruff 格式化" | "当用户问 Docker 问题时按以下步骤排查" |

---

### 8. Loop（循环）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 会话内的**定时重复执行**机制。通过 `/loop` 命令触发，让 Claude 按间隔重复执行某个 prompt |
| **为什么需要** | 需要在会话保持期间定期检查某些状态（部署状态、CI 结果、PR 评论），而不是一直手动刷新 |
| **谁触发** | **用户**手动 `/loop` |
| **怎么触发** | 输入 `/loop [间隔] [prompt]` |
| **什么时候触发** | 当你在一个会话中需要定期执行检查时 |
| **什么场景使用** | 轮询部署进度、定期审查 PR、内置维护 prompt 自动处理未完成工作 |
| **如何使用** | `/loop`（无参=内置维护prompt + Claude 自选间隔）、`/loop 5m`（固定间隔）、`/loop 5m check status`（固定间隔+自定义prompt） |

**/loop 的三种形态**：

| 提供参数 | 示例 | 行为 |
| --- | --- | --- |
| 间隔 + prompt | `/loop 5m check deploy` | 每 5 分钟执行一次指定 prompt |
| 仅 prompt | `/loop check deploy` | Claude 自选间隔（1分钟\~1小时）执行 |
| 无参数 / 仅间隔 | `/loop` 或 `/loop 15m` | 执行内置维护 prompt，自动处理未完成工作 |

**内置维护 prompt 做的事**（按优先级）：

1. 继续对话中未完成的工作
2. 处理当前分支的 PR：审查评论、失败的 CI、合并冲突
3. 清理工作：bug 搜索、代码简化（没有待办时）

---

### 9. Rules（规则文件）

| 维度 | 说明 |
| --- | --- |
| **是什么** | `.claude/rules/*.md` 中的模块化指令文件，是 CLAUDE.md 的补充 |
| **为什么需要** | 当 CLAUDE.md 太长时，按主题拆分成多个规则文件更清晰。支持 **path 作用域**，只在读某个目录/文件时才加载，节省上下文 |
| **谁触发** | **系统自动加载** |
| **怎么触发** | Claude 读某个文件时，触发路径匹配的 rule 文件自动注入上下文 |
| **什么时候触发** | ① 会话启动时加载所有无 path 限制的 rule；② 读文件时加载 path 匹配的 rule |
| **什么场景使用** | 将大型 monorepo 中的不同模块规则分开 |
| **如何使用** | 在 `.claude/rules/` 下创建 `.md` 文件，带可选的 `paths:` frontmatter |

**示例**：

```markdown
# .claude/rules/api-rules.md
---
paths:
  - "src/api/**/*.ts"
---
# API 开发规范
- 所有接口使用 JWT 鉴权
- 返回格式统一为 { code, data, message }
```

**CLAUDE.md vs Rules vs Skill 对比**：

| 维度 | CLAUDE.md | Rules | Skill |
| --- | --- | --- | --- |
| **加载时机** | 每次会话全量加载 | 按路径匹配加载 | 需要时加载（手动/自动匹配） |
| **内容类型** | 永远相关的项目知识 | 按文件类型的规范 | 特定任务的流程指导 |
| **上下文成本** | 每次都消耗 | 匹配路径时才消耗 | 使用时才消耗 |
| **适合场景** | 项目架构、常用命令 | Python 规范、API 规范 | 部署流程、调试步骤 |

---

### 10. Agent Teams（代理团队）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 多个**独立的 Claude Code 会话**协同工作，由一个 team lead 协调，Agent 之间可以互相发消息 |
| **为什么需要** | 当单个会话不够用时（需要多个独立上下文窗口同时工作），或者需要多个专用 Agent 协作完成复杂任务 |
| **谁触发** | **用户**（启用 + 创建 team） |
| **怎么触发** | 设置 `CLAUDE_CODE_EXPERIMENTAL_AGENT_TEAMS=1` → 会话中让 Claude 创建 team → 分配任务 |
| **什么时候触发** | 需要多个独立 Agent 长期协作的重大任务 |
| **什么场景使用** | 大型重构（一个 agent 改前端、一个改后端、一个改测试） |
| **⚠️ 实验性** | 默认关闭，需要显式启用 |
| **与 Subagent 区别** | 你可以直接与 team 成员对话；Subagent 只能向父级报告 |

---

### 11. Plugin（插件）

| 维度 | 说明 |
| --- | --- |
| **是什么** | Skill + Subagent + Hook + MCP 的**打包单元**。一个完整的可分发扩展包 |
| **为什么需要** | ① **分发**：把一组相关的技能打包分享给团队/社区；② **命名空间**：避免命名冲突（`plugin-name:skill-name`）；③ **版本管理**：有版本号，可控更新 |
| **谁触发** | 取决于包含什么——skill 部分用户/Claude、hook 部分系统、subagent 部分 Claude |
| **怎么触发** | 安装后自动生效（hook）或通过 `/plugin-name:skill-name` 调用（skill） |
| **什么时候触发** | 安装了插件后一直有效 |
| **什么场景使用** | 团队共享工具集、从 marketplace 安装社区插件 |
| **如何使用** | `claude --plugin-dir ./my-plugin` 或通过 marketplace 安装 |

**Plugin 目录结构**：

```
my-plugin/
├── .claude-plugin/
│   └── plugin.json          # 清单：name, description, version, author
├── skills/
│   └── hello/
│       └── SKILL.md          # 命名空间为 /my-plugin:hello
├── agents/                   # 自定义 subagent
├── hooks/                    # hook 配置
└── mcp/                      # MCP 服务器配置
```

---

### 12. Goal（目标）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 设置一个**完成条件**，Claude 自动持续工作直到条件满足。每次 turn 结束后用小模型检查条件是否达成 |
| **为什么需要** | 对于需要多个连续步骤的任务，你不需要每步都手动确认。设定"所有测试通过"后，Claude 会自己迭代直到通过 |
| **谁触发** | **用户**输入 `/goal` |
| **怎么触发** | 在对话中输入 `/goal <完成条件>` |
| **什么时候触发** | 当你有一个明确的、可验证的完成标准时 |
| **什么场景使用** | "迁移模块到新 API 直到所有编译通过"、"实现设计文档直到所有验收标准达成" |
| **如何使用** | 先给 Claude 任务，然后 `/goal 所有测试通过` → Claude 自动迭代直到测试通过 |

**Goal vs Loop vs Routines 对比**：

| 维度 | /loop | /goal | Routines |
| --- | --- | --- | --- |
| 触发方式 | `/loop [间隔] [prompt]` | `/goal <条件>` | claude.ai 配置或 `/schedule` |
| 停止条件 | **永不停止**（除非手动） | **条件达成自动停止** | 配置决定（一次/重复） |
| 运行位置 | **会话内** | **会话内** | **Anthropic 云端** |
| 计算机关闭 | ❌ 停止 | ❌ 停止 | ✅ 持续运行 |

---

### 13. Dynamic Workflows（动态工作流）

| 维度 | 说明 |
| --- | --- |
| **是什么** | JavaScript 脚本编排 Subagent 在后台执行。Claude 写脚本，runtime 执行 |
| **为什么需要** | 当任务需要几十到几百个 agent、超出了普通 subagent 的协调能力时。脚本可复现、可审查 |
| **谁触发** | **用户**（通过 `/deep-research` 或让 Claude 创建 workflow） |
| **怎么触发** | Claude 调用 `Workflow` tool |
| **什么时候触发** | 需要大规模自动化时 |
| **什么场景使用** | 全代码库 bug 扫描、500 文件迁移、多来源交叉验证研究 |
| **内置 Workflow** | `/deep-research` — 多角度搜索、获取来源、交叉验证 → 生成引用报告 |

**Subagent vs Agent Teams vs Workflows 对比**：

| 维度 | Subagent | Agent Teams | Dynamic Workflows |
| --- | --- | --- | --- |
| 运行位置 | 主会话内独立上下文 | **独立会话** | 独立运行环境 |
| 通信方式 | 仅上报父级 | Agent 间可互发消息 | 通过脚本变量 |
| 规模 | 几个/次 | 数个长期运行 | 几十到几百个/次 |
| 可重复性 | 子定义可复用 | 团队定义可复用 | **脚本本身可完全复现** |
| 用户交互 | 不能直接交互 | 可直接与任一成员对话 | 不能直接交互 |
| 适用场景 | 隔离子任务 | 长期协作项目 | 大规模自动化审计/迁移 |

---

### 14. Routines（例程）

| 维度 | 说明 |
| --- | --- |
| **是什么** | 保存的 Claude Code 配置（prompt + 仓库 + MCP 连接），在 Anthropic 云端自动运行 |
| **为什么需要** | Loop 只能在会话打开时运行。Routines 关机也能跑 |
| **谁触发** | 触发方式有三种：**定时**（cron）、**API**（HTTP POST）、**GitHub 事件**（PR/release） |
| **怎么触发** | 在 claude.ai/code/routines 配置，或 CLI 中用 `/schedule` |
| **什么时候触发** | 按设定时间/事件自动触发 |
| **什么场景使用** | 每天自动代码审查、每晚自动依赖更新、PR 自动评论、API 触发式部署检查 |
| **⚠️ 状态** | Research Preview |

---

## 三、核心概念关系图

```
用户输入
    │
    ├── 以 "/" 开头 → Command（内置）或 Skill（手动调用）
    │
    └── 自然语言
        │
        ├── 系统自动加载 Memory / Rules（跨会话记忆）
        │
        └── Claude 启动 Agentic Loop
                │
                ├── 读取上下文 ←── CLAUDE.md / Auto memory / Rules
                │
                ├── 决定用什么 Tool
                │   ├── 内置 Tool（Read/Edit/Bash/Grep/...）
                │   ├── MCP Tool（数据库/Jira/...）← 通过 MCP 协议注册
                │   ├── Agent Tool → 启动 Subagent（独立上下文执行）
                │   ├── Workflow Tool → 启动 Dynamic Workflow（脚本编排）
                │   └── Skill Tool → 执行 Skill（prompt 剧本）
                │
                ├── Tool 执行前 → Hook（PreToolUse，拦截检查）
                ├── Tool 执行后 → Hook（PostToolUse，格式化）
                │
                ├── 自动匹配 → Skill（按 description 自动加载）
                │
                └── Turn 结束后 → Hook（Stop，发通知）
                    │
                    ├── 检查 /goal 条件？→ 未达成则继续下一轮
                    └── 有 /loop 定时任务？→ 时间到再执行

独立于会话外运行：
    ├── Routines（云端长期运行，关机不中断）
    ├── Plugin（打包分发 skill + hook + subagent + MCP）
    └── Agent Teams（多独立会话，Agent 间可通信）
```

---

## 四、相似概念深度对比矩阵

### 4.1 Skill vs Command vs MCP

这三个最容易混淆，因为它们都扩展了 Claude 的能力：

| 维度 | Skill | Command（内置） | MCP |
| --- | --- | --- | --- |
| **本质** | **Prompt 剧本**（指令集） | **硬编码逻辑** | **外部工具注册** |
| **运行方式** | 加载到 Claude 上下文中，Claude 用自己的工具执行 | CLI 直接执行固定逻辑 | 注册新 tool 给 Claude 调用 |
| **扩展方向** | 告诉 Claude **"怎么做"**（流程） | 告诉系统 **"做什么"**（操作） | 给 Claude **"新能力"**（新工具） |
| **谁能创建** | 任何人 | 仅 Anthropic | 任何人 |
| **触发方式** | 手动 `/` 或 自动匹配 | 仅手动 `/` | Claude 自动调用（像内置 tool 一样） |
| **跨工具兼容** | ✅ Agent Skills 开放标准 | ❌ Claude Code 私有 | ✅ MCP 开放标准 |
| **何时用** | 需要**标准化工作流**时 | 需要**控制会话行为**时 | 需要**连接外部服务**时 |

**场景决策树**：

```
你想做什么？
├── 控制会话行为（清空/切换模型/退出）→ Command（内置）
├── 指导 Claude 按特定流程做事
│   ├── 简单流程 → Skill（手动或自动触发）
│   └── 需要外部数据/API → Skill + 动态上下文注入
├── 给 Claude 连接外部服务
│   ├── 数据库/API/Jira/Slack → MCP
│   └── 需要打包多个能力分发 → Plugin
└── 让某些事每次都发生（安全/格式化）→ Hook
```

### 4.2 CLAUDE.md vs Skill vs Rules

这三个都是"给 Claude 信息"的机制：

| 维度 | CLAUDE.md | Skill | Rules |
| --- | --- | --- | --- |
| **加载时机** | **每次会话启动**全量加载 | **需要时才加载**（按需） | **按路径匹配加载** |
| **内容类型** | 永远相关的项目知识 | 特定任务的流程指导 | 按文件类型/路径的规范 |
| **上下文成本** | 每次都消耗（所以保持精简） | 仅使用时消耗 | 匹配路径时才消耗 |
| **适合场景** | 项目架构、常用命令、编码规范 | 部署流程、调试步骤、代码审查 | Python 规范、API 规范 |
| **谁写** | 你 | 你 | 你 |

**选择建议**：

- 如果每条信息**每次会话都需要** → CLAUDE.md
- 如果信息**只在特定任务需要** → Skill（自动或手动触发）
- 如果信息**只在读特定类型文件时需要** → Rules（path-scoped）

### 4.3 Subagent vs Agent Teams vs Workflows

| 维度 | Subagent | Agent Teams | Dynamic Workflows |
| --- | --- | --- | --- |
| **规模** | 少数几个/次 | 数个长期运行 | 几十到几百个/次 |
| **隔离级别** | 独立上下文 | **独立会话** | 独立运行环境 |
| **通信方式** | 仅向父级报告 | Agent 间双向通信 | 通过脚本变量 |
| **可重复性** | 子定义可复用 | 团队定义可复用 | **脚本可完全复现** |
| **能否与你交互** | ❌ 不能直接对话 | ✅ 可直接与任一成员对话 | ❌ |
| **适合场景** | 隔离探索性任务 | 长期协作大项目 | 大规模自动化审计/迁移 |
| **谁编排** | Claude 自主决定 | Team lead agent | **脚本本身** |

### 4.4 Hook vs Skill

| 维度 | Hook | Skill |
| --- | --- | --- |
| **执行方式** | **确定性执行**（匹配就运行） | **建议性执行**（Claude 决定是否遵循） |
| **谁控制** | **你控制**是否执行 | **Claude 控制**是否执行 |
| **逻辑类型** | Shell 命令 / HTTP / 固定逻辑 | 自然语言指令 |
| **适合什么** | **必须做的事**（安全门禁、格式化） | **参考性流程**（审查步骤、调试流程） |
| **Claude 感知** | Claude 不直接"知道" hook 存在 | Claude 读取并理解 skill 内容 |
| **举例** | "每次写 Python 文件后自动 ruff 格式化" | "当用户问 Docker 问题时按以下步骤排查" |

### 4.5 Loop vs Goal vs Routines

| 维度 | /loop | /goal | Routines |
| --- | --- | --- | --- |
| **触发方式** | 用户 `/loop` | 用户 `/goal <条件>` | claude.ai 配置或 `/schedule` |
| **停止条件** | **不自动停止** | **条件达成自动停止** | 配置决定 |
| **运行位置** | **会话内** | **会话内** | **Anthropic 云端** |
| **计算机关闭** | ❌ 停止 | ❌ 停止 | ✅ 持续运行 |
| **适合场景** | 会话中定期轮询 | 自动迭代直到完成 | 持续运行的自动化任务 |

---

## 五、版本演进关键变更

| 旧名称 | 新名称 | 说明 |
| --- | --- | --- |
| Custom Commands | **Skills** | `.claude/commands/` 仍兼容，但 Skills 功能更丰富 |
| Slash Commands | **Commands** | "Slash"从产品文案中移除 |
| Headless mode | **Non-interactive mode** | 同一 `-p` 参数，行为相同 |
| `TodoWrite` tool | **TaskCreate/List/Get/Stop/Update** | 更完整的任务管理工具集 |

---

## 六、概念速查表

| 概念 | 本质 | 触发者 | 触发方式 | 加载/运行时机 | 配置位置 |
| --- | --- | --- | --- | --- | --- |
| **Tool** | 原子操作 | Claude | Agentic loop 自主决定 | 每次需要时 | 内置，不可配置 |
| **Command**（内置） | 硬编码逻辑 | 用户 | `/xxx` 手动输入 | 用户需要时 | 内置 |
| **Skill** | Prompt 剧本 | 用户 / Claude | 手动 `/` 或自动匹配 | 需要时按需加载 | `.claude/skills/` |
| **CLAUDE.md** | 你写的记忆 | 系统 | 自动加载 | 每次会话启动 | `./CLAUDE.md` |
| **Auto memory** | Claude 写的记忆 | 系统 / Claude | 自动加载 + 自动写入 | 启动时 + 对话中 | `~/.claude/projects/` |
| **Rules** | 路径作用域指令 | 系统 | 按路径自动加载 | 读匹配文件时 | `.claude/rules/` |
| **Subagent** | 独立上下文 Agent | Claude | 调用 `Agent` tool | Claude 决定需要时 | `.claude/agents/` |
| **MCP** | 外部工具注册 | Claude | 像内置 tool 一样调用 | Claude 需要外部服务时 | `.mcp.json` |
| **Hook** | 确定性自动化 | 系统 | 生命周期事件触发 | 固定事件点 | `settings.json` |
| **Loop** | 会话内定时任务 | 用户 | `/loop` 命令 | 设定间隔重复 | 会话中配置 |
| **Goal** | 条件驱动执行 | 用户 | `/goal <条件>` | 条件未达成持续执行 | 会话中配置 |
| **Agent Teams** | 多会话协作 | 用户 | 环境变量 + 创建 team | 需要时 | 实验性功能 |
| **Plugin** | 能力打包单元 | 取决于内容 | 安装后自动/手动 | 安装后持续有效 | 独立目录 |
| **Workflows** | 脚本编排 Subagent | 用户 | Claude 调用 `Workflow` tool | 大规模自动化时 | 脚本 |
| **Routines** | 云端自动化 | 定时/API/GitHub | 自动触发 | 设定时间/事件 | claude.ai |
| **Artifact** | 实时交互网页 | Claude | 调用 `Artifact` tool | 需要可视化输出时 | 内置 |

---


# 3. skill介绍和使用

# 3. Prompt提示词工程


# 4. 上下文工程


# 4. Harness驾驭工程


# 5. Loop循环工程

