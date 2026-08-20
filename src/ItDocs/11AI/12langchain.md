# 1. 你了解过哪些 AI Agent 开发框架？

我重点了解过 **LangChain**、**LangGraph** 和 **LlamaIndex**。

**LangChain** 提供模型、Prompt、工具、Agent 和中间件等通用抽象，集成范围比较广，适合快速构建工具调用、RAG、SQL 查询等 AI 应用。

**LangGraph** 更偏底层的状态与流程编排。面对循环、分支、并行、断点恢复和人工审批等复杂流程时，可以使用图结构显式控制 Agent 的执行路径。现在 LangChain 的 Agent 也运行在 LangGraph 之上，因此二者更多是上下层关系，而不是互相替代的竞争关系。

**LlamaIndex** 的优势集中在数据接入、文档解析、索引和检索，适合企业知识库、文档问答和复杂 RAG 等数据密集型应用。

除此之外，我也了解 **OpenAI Agents SDK** 和 **CrewAI**。前者适合以 OpenAI 模型为主的轻量 Agent，后者擅长使用角色和任务表达多 Agent 协作。不过在通用 Python Agent 开发岗位中，我会优先掌握 LangChain、LangGraph 和 LlamaIndex，再根据公司的技术栈补充其他框架。

**选型时应该考虑什么？**

1. **判断任务是否真的需要 Agent**。如果步骤固定、规则明有分类和目标识别模型训练经验，大模型应用开发经验确，普通函数或工作流通常更便宜、更稳定。
2. **找到项目真正困难的那一层**。模型和工具接入最费力，LangChain 更自然；难点集中在私有数据、文档解析和检索质量，LlamaIndex 更贴近问题；业务路径包含复杂分支、循环和状态恢复，才轮到 LangGraph 发挥优势。
3. **考虑生产约束**。中断后能否恢复，敏感动作是否需要审批，重复执行会不会产生副作用，不同用户的数据能否隔离，出错后是否留有完整轨迹。

**三个框架怎么配合？**

LangChain、LangGraph 和 LlamaIndex 并不一定三选一。一个企业知识库 Agent 可以先用 LlamaIndex 处理文档、建立索引并提供检索结果，再把这项检索能力包装成 Tool，交给 LangChain Agent 决定何时调用。如果外围还存在查询改写、答案校验、人工审核和失败恢复，就让 LangGraph 负责这些步骤如何衔接。

**面试总结**：框架说得越多，面试官越可能继续追问。更稳妥的回答方式是围绕 LangChain、LangGraph 和 LlamaIndex 展开，说明各自定位，再补充其他框架有所了解。最后一定要把框架特点落到真实场景：简单 Agent 为什么选择 LangChain，复杂审批流程为什么使用 LangGraph，企业知识库为什么考虑 LlamaIndex。

# 2. 请你谈谈对 LangChain 中核心概念「Chain」的理解，以及它的核心作用与设计理念。

我理解的 Chain，不是某一个固定的类，而是一种应用编排思路：把 Prompt、模型、检索器、输出解析器和自定义逻辑等步骤，按照明确的数据流连接起来，让上一步的输出成为下一步的输入，最终形成一个可以整体执行的流程。

在现在的 LangChain 里，Chain 最重要的技术基础是 **Runnable**。每个步骤都尽量遵守统一的输入输出和执行接口，再通过 LCEL 的 `|` 做串行组合，或者通过字典、`RunnableParallel` 做并行组合。

组合后的整条 Chain 本身仍然是 Runnable，所以可以继续嵌套，也能统一使用 `invoke`、`ainvoke`、`batch` 和 `stream` 等能力。

它的核心设计理念是「**组合优于堆积封装**」。开发者只关注每一步做什么、数据怎么流动，框架负责把执行方式、配置、重试、回退和追踪等通用能力接到整条流程上。

**需要注意版本边界**：`LLMChain`、`SequentialChain` 属于旧式 Chain API，LangChain v1 已把这类能力移入 `langchain-classic`，适合维护旧项目，不应再作为新项目的首选写法。

**三种编排方式怎么选？**

1. **确定性的线性或分支流程**可以用 Runnable 和 LCEL。
2. **Agent** 让模型在运行时动态决定下一步。
3. **带循环、持久状态和人工审批的复杂工作流**，则更适合用 LangGraph。

**LCEL 不只是语法糖**：`prompt | model | parser` 声明的是三个 Runnable 的组合关系，LangChain 会据此构造一个 `RunnableSequence`。在这个序列里，前一步的输出会作为后一步的输入。组合后的整体仍然通过统一的 `invoke` 接口执行，可以异步调用 `await chain.ainvoke(...)`，批量处理 `chain.batch([...])`，流式输出 `chain.stream(...)`。

**面试总结**：回答 Chain 时，先说 Chain 是确定性的数据流编排，再落到现代实现：Runnable 是统一执行与组合接口，LCEL 是声明组合关系的表达方式。最后补上版本意识：新项目写固定流程优先使用 Runnable 与 LCEL，动态工具决策使用 `create_agent`，复杂有状态编排再使用 LangGraph。

# 3. LangChain 的底层架构与实现原理是什么？

当前 LangChain v1 更像一套面向 Agent 的分层开发框架，而不只是将 Prompt 串起来的 Chain 工具。

底层的 `langchain-core` 定义 **Message**、**Model**、**Tool** 和 **Runnable** 等标准协议；不同模型厂商的独立集成包负责把自己的请求与响应适配到这些协议，因此应用层可以使用相对统一的方式切换模型和工具。

在执行层，**Runnable** 统一了组件的同步、异步、批处理和流式调用方式。对于步骤固定的流程，可以使用 LCEL 组合 Prompt、Model 和 Parser；对于需要模型自主选择工具的任务，则使用 `create_agent` 创建 Agent。

`create_agent` 会把模型节点和工具节点编译成 **LangGraph** 状态图。模型读取消息后生成 `AIMessage`；如果其中包含 `tool_calls`，运行时执行对应工具，并把结果包装成带相同调用 ID 的 `ToolMessage` 写回状态；模型再次读取工具结果并继续判断，直到生成最终回答。

**架构分成四层：**

1. **核心协议层**：统一组件的数据结构与调用接口，包括 Message、Runnable、Model、Tool。
2. **集成适配层**：屏蔽模型、向量库和外部服务差异，例如 `langchain-openai` 等独立集成包。
3. **Agent 开发层**：提供高层 Agent 组装与扩展能力，例如 `create_agent`、Middleware、Structured Output。
4. **编排运行层**：管理状态、循环、路由、持久化和恢复，由 LangGraph Runtime 负责。

**数据应该放在哪里？**

| 数据 | 作用 | 示例 |
| --- | --- | --- |
| State | 执行中不断变化的数据 | 消息、当前步骤、工具结果 |
| Context | 一次调用期间不变的可信依赖 | 用户 ID、租户、权限 |
| Store | 跨线程保存的数据 | 用户偏好、长期事实 |

**Middleware 做什么？**

Middleware 提供模型和工具调用前后的扩展点。请求进入模型前，可以根据用户身份生成系统提示词，或者在历史消息过长时先做摘要；模型决定调用工具后，可以先检查权限，敏感动作还可以暂停等待审批。

**面试总结**：从一次请求的执行过程展开：`langchain-core` 使用标准协议隔离厂商差异，`create_agent` 编译成 LangGraph 状态图，State 保存可变状态，Context 提供可信依赖，Store 保存跨线程数据，Middleware 负责权限、重试、摘要和人工审批。

# 4. 使用 LangChain 构建 Agent 的核心步骤是什么？

我通常分七步构建 LangChain Agent。

1. **明确任务边界**，包括 Agent 能做什么、不能做什么、何时结束，以及什么结果算成功。
2. **选择支持所需工具调用和结构化输出能力的模型**，并把数据库、搜索和业务 API 封装为职责单一、Schema 清晰的 Tools。
3. **通过** `system_prompt` 约束行为与输出，如果结果还要交给程序处理，则使用 `response_format` 定义结构化输出。
4. **通过** `create_agent` 组装 Agent，它底层使用 LangGraph，在模型判断、工具执行和工具结果回传之间循环。
5. **补充状态与安全能力**，使用 Checkpointer 按 `thread_id` 保存当前线程状态，使用 Store 管理跨线程信息，通过 Middleware 添加重试、摘要、权限控制和人工审批。
6. **根据场景选择同步、异步或流式调用**，并设置超时、并发和取消策略。
7. **先单测 Tool，再测试 Agent 的工具选择与调用轨迹**，最后通过 Trace 观察模型调用、工具参数、耗时、Token 和异常。

**Tool 为什么要小而清楚？**

模型主要依靠名称、描述和参数 Schema 来判断能不能调用。一个工具同时负责查询、退款和通知，模型就更容易选错动作；参数没有类型与范围约束，运行时也很难拦住错误输入。所以 Tool 应先做到职责单一、名称清楚、输入输出容易理解。

**上线前检查什么？**

1. 任务和停止条件是否明确。
2. Tool 是否职责单一，并在服务端校验权限。
3. 有副作用的操作是否具备幂等和审批。
4. Checkpointer 与 Store 是否使用持久化实现并做好用户隔离。
5. 是否设置超时、重试上限、并发和成本预算。
6. 是否覆盖工具、轨迹和端到端评测。
7. 是否能够追踪一次失败运行的完整调用链。

**面试总结**：沿着「边界、能力、约束、组装、状态、交互、验证」七步回答。先把这七步讲清楚，说明你构建的不是一个只能演示的 Agent，而是一个有边界、有状态、可测试、可观测的业务系统。

# 5. 在 LangChain 中，如何为 Agent 注册工具？

LangChain 中注册 Tool 的本质，是同时向模型提供一份工具说明，并向运行时提供一个真正可执行的函数。工具说明主要包含名称、用途和参数 Schema，模型根据它选择工具并生成参数，LangChain 再执行对应函数。

**最常用的实现方式有四种：**

1. **简单的已有函数**，可以带上类型注解和 docstring 后直接放入 `tools`。
2. **大多数业务工具使用** `@tool`，便于自定义名称、描述和参数 Schema。
3. **需要在运行时组装同步函数、异步函数和 Schema 时**，可以使用 `StructuredTool`。
4. **工具需要封装客户端、维护资源或定制执行过程时**，再继承 `BaseTool`。

**可信参数如何注入？**

需要区分两类参数：

- **任务参数**（城市、关键词、订单号）：模型根据用户问题生成。
- **可信参数**（用户 ID、租户、权限、当前状态）：应用运行时注入。

LangChain v1 使用 `ToolRuntime` 把这些可信信息送进工具，用户身份、租户和依赖属于本次调用上下文，从 `runtime.context` 读取；当前会话消息和短期状态放在 `runtime.state`；跨会话仍要保留的长期数据，才进入 `runtime.store`。

**错误应该怎么处理？**

- **参数格式错误**：先让 Schema 拦截，再把可修正的信息交给模型重新填写。
- **业务结果**（库存不足、没有权限）：工具应该把原因说清楚，让 Agent 决定换条路径或直接告知用户。
- **网络超时、限流**：可以做有上限的重试，但必须同时设置退避和总超时。
- **程序 Bug**：不应该被统一转换成「调用失败」后继续执行，否则系统会掩盖真正的问题。

**面试总结**：先说明 Tool 是「模型可见的调用合同 + 运行时可执行函数」，再讲清楚四种常用定义方式的选择边界。可信身份和权限通过 `ToolRuntime` 注入，不能交给模型生成。最后补充 Schema 校验、异步 I/O、错误分类、幂等和权限治理。

# 6. LangChain 如何实现短期记忆和长期记忆？

在 LangChain v1 中，可以用一句话区分两类记忆：

**短期记忆 = State + thread_id + Checkpointer**
**长期记忆 = namespace/key + Store**

**短期记忆属于当前会话线程**。Agent State 保存消息、当前步骤和中间结果；Checkpointer 按 `thread_id` 保存状态快照。使用同一个 `thread_id` 再次调用时，可以恢复前面的对话和执行状态。

**长期记忆不应该绑定某个线程**，而是保存到 Store。Store 使用 namespace 和 key 组织数据，namespace 通常包含租户、用户和记忆类型。即使用户新建了线程，只要使用相同的可信用户身份和 namespace，仍然可以读取以前保存的偏好或经验。

**消息太多怎么办？**

常用策略有三种：

- **裁剪**：只选择部分消息进入本次模型上下文，但持久状态仍会继续增长。
- **删除**：从 State 中永久移除旧消息，信息不可恢复。
- **摘要**：把早期历史压缩成简短语义摘要，可能遗漏细节或逐轮失真。

**生产环境要注意什么？**

1. 使用数据库型 Checkpointer 和 Store，并做好租户隔离。
2. 用户 ID、tenant_id 和 user_id 必须来自可信身份体系。
3. 系统要能够去重、更新和过期淘汰，同时支持用户查看、更正、导出和删除。
4. 评测不能只看「成功写入多少条」，而要沿着整条链路检查：这条信息是否值得写入，相关问题能否召回，无关问题会不会误召回，注入模型后是否真正改善答案。

**旧 Memory 还能用吗？**

旧教程常见的 `ConversationBufferMemory`、`ConversationSummaryMemory` 等属于 Chain 时代的抽象，目前主要位于 `langchain-classic`，适合维护存量项目。LangChain v1 新项目更推荐使用 `AgentState + Checkpointer` 管理线程内状态，`Store + namespace/key` 管理跨线程长期记忆。

**面试总结**：首先说清楚两条主线：短期记忆是线程级 State，由 Checkpointer 按 `thread_id` 保存；长期记忆是跨线程数据，由 Store 按 namespace 和 key 管理。接着说明 `ToolRuntime` 的边界：模型只生成任务参数，可信用户身份通过 Context 注入。最后补充长上下文治理和生产要求。

# 7. LangChain 和 LlamaIndex 有什么区别？

LangChain 和 LlamaIndex 现在都能构建 Agent、调用工具和实现 RAG，但它们的默认入口与优势重心不同。

**LangChain 更偏通用 Agent 组装**。它统一模型、消息、工具、结构化输出和中间件等接口，适合快速连接不同模型供应商、业务 API 和外部工具。当前 LangChain Agent 底层运行在 LangGraph 之上，需要复杂状态、暂停恢复和人工审批时，可以进一步使用 LangGraph 编排。

**LlamaIndex 更偏数据与上下文增强**。它在数据接入、文档解析、切分、索引、检索、重排和 Query Engine 等环节积累更深，适合企业知识库、复杂文档问答和数据密集型 Agent。它也提供 Agent 和 Workflow，因此不能简单说它只能做 RAG。

**选型时应该看项目的主要难点：**

| 项目主要风险 | 优先评估 | 原因 |
| --- | --- | --- |
| 模型和业务工具太多，集成复杂 | LangChain | 通用组件和工具接口更自然 |
| 文档解析、切分和检索质量差 | LlamaIndex | 数据与上下文链路抽象更细 |
| 流程需要暂停恢复和人工审批 | LangGraph，可搭配 LangChain | 状态与执行控制是核心能力 |
| 同时需要复杂检索和复杂流程 | LlamaIndex + LangChain/LangGraph | 数据层与编排层分别选择合适组件 |

**两者如何组合？**

最常见的组合边界是 Tool。LlamaIndex 负责加载数据、构建索引和实现 Query Engine，对外暴露一个「查询企业知识库」的函数，再包装成 LangChain Tool。LangChain Agent 判断什么时候调用，外层如果还有审批、重试和恢复，则交给 LangGraph。

**常见误区**：不要被框架名字或早期教程限制住。LangChain 并不只会把 Prompt 串成 Chain，当前主线已经转向 Agent；LlamaIndex 也不只是一个 RAG 工具，更不是向量数据库。项目可以通过 Tool 或服务接口组合两套框架，但只有数据层和 Agent 编排层确实各自存在独立难题时，这种组合才有收益。

**面试总结**：不要再使用「LangChain 做 Chain，LlamaIndex 做 RAG」的过时标签。真正的区别是设计重心。最后把选型落到业务：工具与模型集成是主要难点，优先考虑 LangChain；数据处理和检索质量是主要难点，优先考虑 LlamaIndex；两边都复杂时，让 LlamaIndex 管数据层，让 LangChain 或 LangGraph 管 Agent 和运行层。

# 8. 请你谈谈 LangChain4j 这类 Java 生态的 LangChain 衍生框架，主要帮开发者解决了哪些核心问题？它的核心适用场景是什么？

**LangChain4j 不是 Python LangChain 的官方 Java 移植版**，而是一套独立开发、按照 Java 习惯设计的开源 LLM 应用框架。

它主要解决三类问题：

1. **供应商 API 不统一**，框架通过 `ChatModel`、`EmbeddingModel`、`EmbeddingStore` 等接口，把常见模型和向量库接到相对一致的 Java API 上。
2. **LLM 应用胶水代码太多**，AI Services 可以像 Spring Data JPA 一样声明 Java 接口，再把 Prompt、输出解析、Tools、Chat Memory 和 RAG 组装起来。
3. **Java 工程接入成本**，官方生态能融入 Spring Boot、Quarkus、Helidon 和 Micronaut，复用依赖注入、配置、测试和监控体系。

**核心适用场景：**

已有 Java 技术栈，要做企业知识库问答、智能客服、文档抽取、内容生成，或者让模型调用现有 Java 服务的团队。特别是业务已经沉淀在 Spring Boot 或 Quarkus 中时，不必为了增加 AI 能力再单独维护一套 Python 服务。

**AI Services 解决什么问题？**

开发者声明一个 Java 接口，LangChain4j 在运行时提供代理实现。它很像 Spring Data JPA 或 Retrofit：我们描述「服务要暴露什么方法」，框架负责把方法参数变成消息，再把模型响应转换成方法返回值。AI Service 代理先把方法参数组织成消息，Retriever 再补充知识库内容。模型如果判断需要查询订单，就调用 Tools；工具结果返回后，模型继续生成答案。

**Chat Memory 等于聊天档案吗？**

Chat Memory 保存的是下一次要喂给模型的上下文，可以发生淘汰、压缩或注入；完整聊天记录则是产品实际展示和审计所需的事实记录。官方文档明确说明 LangChain4j 当前提供的是 memory，不替应用保存完整 history。

**边界要清楚：**

- 统一 API 不等于厂商能力完全一致。
- Chat Memory 不等于完整历史。
- Guardrails 不等于权限系统。
- 实验性或 beta 模块上线前必须锁版本、做回归评测和可观测验证。

**面试总结**：第一句话要先把定位说准：LangChain4j 不是 Python LangChain 的官方 Java 移植，而是一套独立、遵循 Java 习惯的 JVM LLM 应用框架。接着回答它解决什么问题，然后给出场景。最后主动说出边界会很加分。

# 9. 请你详细说说 LangChain 和 LangGraph 的核心区别是什么？

我不会把 LangChain 和 LangGraph 理解成两个互相替代的竞品。按照当前官方定位，**LangChain v1 是高层 Agent 开发框架**，负责提供模型、工具、结构化输出和 middleware 等常用能力。

**LangGraph 则是低层的 Agent 编排框架与运行时**，让开发者直接设计状态、节点、路由、并行、中断和恢复。

两者最关键的关系是，**LangChain v1 的** `create_agent` 构建在 LangGraph 之上，返回一个编译后的图。也就是说，LangChain Agent 不是脱离 LangGraph 运行的另一套引擎，它已经继承了 LangGraph 的状态、持久化、流式输出、durable execution 和 human-in-the-loop 等运行能力。

**核心差异在于抽象层级：**

| 对比维度 | LangChain v1 | LangGraph |
| --- | --- | --- |
| 官方定位 | 高层 Agent 开发框架 | 低层 Agent 编排框架与运行时 |
| 主要入口 | `create_agent`、模型、工具、middleware、结构化输出 | `StateGraph`、State、Node、Edge、`Command`、Send、Subgraph |
| 控制流 | 标准 Agent loop 已搭好，可通过 middleware 定制行为 | 开发者显式定义顺序、条件路由、循环、并行、动态分发和子图 |
| 更适合 | 标准工具调用 Agent、客服助手、数据查询助手、快速原型 | 长流程、多阶段审批、确定性与 Agent 混排、复杂并行、多 Agent 系统 |

**什么时候下沉 LangGraph？**

1. 需求是常见的「模型判断 -> 调用工具 -> 返回模型」循环，优先用 LangChain。
2. 业务需要显式控制多个阶段，让确定性步骤与 Agent 步骤混排，或者要处理复杂并行、长期暂停和多 Agent 协作，直接用 LangGraph。
3. 很多真实项目最合适的方案，是用 LangChain 构建 Agent，再把它作为 LangGraph 的节点或子图。

**Middleware 与图编排有何不同？**

LangChain middleware 很适合改造标准 Agent loop，例如在模型调用前动态生成提示词、裁剪消息、选择模型和工具。这些逻辑都围绕 Agent、Model、Tool 的生命周期展开，不需要重新设计整张图。

LangGraph 节点和边处理的是更一般的流程结构。它可以让分类节点进入完全不同的子流程，让多个节点并行后再汇合，也可以把数据库写入、人工表单、规则引擎和一个完整 Agent 放在同一张图中。

**面试总结**：先把关系定准：LangChain v1 是高层 Agent 框架，LangGraph 是低层编排框架与运行时，`create_agent` 构建在 LangGraph 上。接着讲核心边界：LangChain 默认提供常见模型与工具循环，LangGraph 让开发者显式控制整个流程。最后主动纠正常见误区。

# 10. LangGraph 相比于 LangChain 有哪些核心优势？更适配哪些 Agent 场景？

LangGraph 的核心优势不是「LangChain 没有这些能力」，而是把复杂 Agent 的运行过程变成**显式、可持久化、可观察、可恢复的业务状态机**。

**核心优势：**

1. **显式控制**：State、节点边界、并行汇合、检查点、中断与恢复都要进入执行语义。
2. **可靠执行**：checkpoint、interrupt 和节点级容错让长任务可以暂停与恢复。
3. **工程化**：流式事件、短期与长期记忆以及部署服务，可以支撑有状态、长时间运行的生产系统。

**更适配的 Agent 场景：**

- **高风险审批**：退款、付款、删库、发送正式邮件、发布内容等动作，需要人看一眼。
- **跨小时或跨天任务**：深度研究、报表生成、代码迁移和跨系统工单可能持续数十分钟到数天。
- **并行深度研究**：先拆主题，再并行搜索多个来源，随后交叉验证、合并证据。
- **多 Agent 协作**：不同团队也可以分别维护研究、合规、财务等子图。
- **自纠错 RAG**：在证据不足时改写查询并重新检索。
- **需要精确调试的复杂工作流**：显式节点、状态历史和分叉能力才会真正转化为调试价值。

**哪些场景不必用 LangGraph？**

- 一个只需要查天气、查订单、总结文档的标准工具调用 Agent。
- 动态提示词、模型切换、工具过滤、消息摘要、重试、护栏和敏感工具审批，优先看看 LangChain middleware 能不能解决。
- 如果流程只有固定的 Prompt、模型和解析器，也不一定需要 Agent。

**两种编排 API 怎么选？**

- **Graph API**：分支和循环很多，需要看清完整拓扑；多路并行后汇合，或多 Agent 交接。
- **Functional API**：已有一大段普通 Python 流程，希望增加检查点、任务恢复和人工暂停；线性流程加少量条件和人工确认。

**面试总结**：先把关系说准：LangChain v1 的 Agent 本身构建在 LangGraph 上，所以两者不是互斥框架。接着抓住三条主线：控制、可靠、工程化。场景上，LangGraph 更适合高风险审批、跨小时或跨天任务、并行深度研究、多 Agent 协作。最后给出边界：标准工具调用 Agent 优先用 LangChain `create_agent`，简单审批优先用 middleware。

# 11. LangChain 大版本升级有哪些核心变化？

LangChain 的版本演进可以抓住四条主线。

1. **拆分核心与集成**：稳定的消息、模型、Tool 和 Runnable 协议放进 `langchain-core`，第三方模型与向量库则迁到 `langchain-community` 或独立集成包。
2. **使用 Runnable 和 LCEL 统一组件调用**：Prompt、Model、Parser 等组件拥有一致的 `invoke`、`batch`、`stream` 和异步接口。
3. **将 Agent 运行时转向 LangGraph**：传统 Agent 执行器适合简单循环，LangGraph 将执行过程显式表示为状态图，成为 LangChain Agent 的底层运行基础。
4. **LangChain v1 进一步聚焦 Agent**：`create_agent` 成为高层入口，middleware 负责动态提示词、工具控制、摘要、重试和人工介入等横切能力，旧 Chain 等接口进入 `langchain-classic`。

**升级时要注意什么？**

1. 动手前先锁定当前依赖和可复现环境，再阅读目标版本的迁移指南。
2. 顺着新的分层检查兼容关系：`langchain`、LangGraph 和模型集成包各自有更新节奏。
3. 工具调用、结构化输出、流式响应和持久化仍要分别回归。
4. 付款、发消息等有副作用的路径还要先在隔离环境验证幂等，再做小流量发布。

**演进方向是什么？**

LangChain 的演进方向是稳定核心、解耦集成、组合确定性流程、图化 Agent 运行时。这样既能回答「升级了什么」，也能解释「为什么要升级」。

**面试总结**：围绕四个架构节点回答：拆分核心与第三方集成、使用 Runnable 和 LCEL 统一组件协议、使用 LangGraph 承担复杂 Agent 运行时、v1 通过 `create_agent`、middleware 和 `langchain-classic` 重新聚焦 Agent。最后说出自己的判断：LangChain 的演进方向是稳定核心、解耦集成、组合确定性流程、图化 Agent 运行时。

# 12. Deep Research 的实现逻辑和适用场景是什么？

Deep Research 不是 LangChain 核心包里的一个固定开关，而是一类面向开放问题的研究型 Agent 架构。LangChain 团队提供了 `open_deep_research` 参考实现，也提供了更通用的 Deep Agents SDK。

**核心流程：**

1. **澄清问题并确定范围**：用户只说「研究某家公司」时，系统需要确认是在关注投资价值、技术路线还是就业风险。
2. **生成 Research Brief**：把用户目标、研究维度、时间范围、来源要求和最终交付形式整理成稳定的成功标准。
3. **Supervisor 拆分子课题**：只有相对独立的问题才适合并行。
4. **Researcher 并行检索与核验**：每个研究员只处理一个主题，并保留来源信息。
5. **压缩证据并检查研究缺口**：不是把所有网页原文塞回 Supervisor，而是压缩成带出处的关键证据。
6. **统一生成最终报告**：由一个写作阶段统一组织论证、处理重复内容。

**为什么需要子 Agent？**

- **隔离上下文**：每个 Researcher 只处理一个子课题，最终只返回压缩后的结论和来源。
- **并行加速**：相互独立的研究任务可以同时执行，整体等待时间会下降。
- **保持一致性**：并行搜证据、统一写报告，更容易保持全文一致性。

**如何保证研究质量？**

1. **看来源是否值得信**：官方文档、论文、监管文件和一手资料通常更接近原始事实。
2. **确认它真的支持当前结论**：报告应把事实、推断和不确定性分开。
3. **遇到冲突时，继续追查**：发布时间、统计口径和原始出处。

**适用场景：**

适合竞品分析、技术调研、文献综述、供应商尽调等开放式、多来源、可拆分任务，不适合一次搜索就能回答的简单事实，也不适合子任务高度依赖的强耦合工作。

**生产环境必须：**

1. 限制并发、迭代、Token 和搜索预算。
2. 对网页提示词注入、来源可信度和高风险结论进行人工复核。
3. 对于金融、医疗、法律和安全决策，Deep Research 只能辅助资料整理，不能因为报告带有引用就取消专家复核。

**面试总结**：先说明它是一类研究型 Agent 架构，而不是 LangChain 核心包中的一个开关。核心流程是「明确范围、生成 Brief、拆分子课题、并行检索、压缩核验、统一写作」。最后要主动说明边界：它适合开放、多来源、可拆分且报告价值较高的任务；上线时必须控制并发、迭代、Token、搜索费用和提示词注入风险，并保留来源追溯与人工复核。

**参考资料：**

- [LangChain 官方文档](https://docs.langchain.com/oss/python/langchain/overview)
- [LangGraph 官方文档](https://docs.langchain.com/oss/python/langgraph/overview)
- [LlamaIndex 官方文档](https://developers.llamaindex.ai/python/framework/)
- [LangChain4j 官方文档](https://docs.langchain4j.dev/intro/)
- [LangSmith 官方文档](https://docs.langchain.com/langsmith/overview)

